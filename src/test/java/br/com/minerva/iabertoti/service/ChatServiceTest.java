package br.com.minerva.iabertoti.service;

import br.com.minerva.iabertoti.model.Autor;
import br.com.minerva.iabertoti.model.DocumentoConhecimento;
import br.com.minerva.iabertoti.model.Mensagem;
import br.com.minerva.iabertoti.observer.MetricasObservador;
import br.com.minerva.iabertoti.strategy.EcoStrategy;
import br.com.minerva.iabertoti.strategy.EstrategiaResposta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class ChatServiceTest {

    private ChatService chatService;
    private ConhecimentoService conhecimentoService;
    private MetricasObservador metricasObservador;
    private EcoStrategy ecoStrategy;

    @BeforeEach
    void setUp() {
        conhecimentoService = new ConhecimentoService();
        metricasObservador = new MetricasObservador();
        ecoStrategy = new EcoStrategy();

        chatService = new ChatService(
                ecoStrategy,
                conhecimentoService,
                metricasObservador,
                List.of(ecoStrategy)
        );
    }

    @Test
    @DisplayName("Deve enviar pergunta, gerar resposta via Strategy e notificar Observers")
    void deveEnviarPerguntaComSucesso() {
        AtomicBoolean observadorChamado = new AtomicBoolean(false);
        chatService.adicionarObservador(resposta -> observadorChamado.set(true));

        chatService.enviarPergunta("Como funciona o Knowledge Assistant?");

        List<Mensagem> mensagens = chatService.getMensagens();
        assertEquals(2, mensagens.size());
        assertEquals(Autor.USUARIO, mensagens.get(0).autor());
        assertEquals("Como funciona o Knowledge Assistant?", mensagens.get(0).conteudo());
        assertEquals(Autor.ASSISTENTE, mensagens.get(1).autor());
        assertTrue(mensagens.get(1).conteudo().contains("EcoStrategy"));

        assertTrue(observadorChamado.get());
        assertEquals(1, metricasObservador.getTotalRespostas());
        assertTrue(metricasObservador.getTotalCaracteres() > 0);
        assertNotNull(metricasObservador.getUltimaRespostaEm());
    }

    @Test
    @DisplayName("Deve carregar documento via arquivo e registrar aviso na sessao")
    void deveCarregarDocumentoArquivo() throws IOException {
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "manual.txt",
                "text/plain",
                "Conteudo do manual".getBytes(StandardCharsets.UTF_8)
        );

        DocumentoConhecimento doc = chatService.carregarDocumentoArquivo(arquivo);

        assertNotNull(doc);
        assertEquals("manual.txt", doc.nome());
        assertTrue(conhecimentoService.temConhecimentoCarregado());

        List<Mensagem> mensagens = chatService.getMensagens();
        assertEquals(1, mensagens.size());
        assertTrue(mensagens.get(0).conteudo().contains("manual.txt"));
    }

    @Test
    @DisplayName("Deve limpar documento da base de conhecimento")
    void deveLimparDocumento() {
        chatService.carregarDocumentoTexto("dados.txt", "conteudo");
        assertTrue(conhecimentoService.temConhecimentoCarregado());

        chatService.limparDocumento();

        assertFalse(conhecimentoService.temConhecimentoCarregado());
        assertTrue(chatService.getMensagens().get(chatService.getMensagens().size() - 1)
                .conteudo().contains("limpa"));
    }

    @Test
    @DisplayName("Deve alternar entre Strategies disponiveis")
    void deveAlternarEstrategia() {
        EstrategiaResposta mockEstrategia = pergunta -> "Resposta Customizada: " + pergunta;
        ChatService serviceComMultiplas = new ChatService(
                ecoStrategy,
                conhecimentoService,
                metricasObservador,
                List.of(ecoStrategy, mockEstrategia)
        );

        assertEquals("EcoStrategy", serviceComMultiplas.getNomeEstrategiaAtual());

        serviceComMultiplas.selecionarEstrategia(mockEstrategia.getClass().getSimpleName());
        assertEquals(mockEstrategia.getClass().getSimpleName(), serviceComMultiplas.getNomeEstrategiaAtual());
    }
}
