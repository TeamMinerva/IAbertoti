package br.com.minerva.iabertoti.command;

import br.com.minerva.iabertoti.observer.MetricasObservador;
import br.com.minerva.iabertoti.service.ChatService;
import br.com.minerva.iabertoti.service.ConhecimentoService;
import br.com.minerva.iabertoti.strategy.EcoStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandTest {

    private ChatService chatService;
    private ConhecimentoService conhecimentoService;

    @BeforeEach
    void setUp() {
        conhecimentoService = new ConhecimentoService();
        EcoStrategy ecoStrategy = new EcoStrategy();
        chatService = new ChatService(
                ecoStrategy,
                conhecimentoService,
                new MetricasObservador(),
                List.of(ecoStrategy)
        );
    }

    @Test
    @DisplayName("EnviarPerguntaCommand deve executar envio de pergunta no ChatService")
    void deveExecutarEnviarPerguntaCommand() {
        Comando comando = new EnviarPerguntaCommand(chatService, "Qual o objetivo do sistema?");
        comando.executar();

        assertEquals(2, chatService.getMensagens().size());
        assertEquals("Qual o objetivo do sistema?", chatService.getMensagens().get(0).conteudo());
    }

    @Test
    @DisplayName("CarregarDocumentoCommand deve carregar arquivo no ChatService")
    void deveExecutarCarregarDocumentoCommand() {
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "requisitos.txt",
                "text/plain",
                "Requisitos de software".getBytes(StandardCharsets.UTF_8)
        );

        Comando comando = new CarregarDocumentoCommand(chatService, arquivo);
        comando.executar();

        assertTrue(conhecimentoService.temConhecimentoCarregado());
        assertEquals("requisitos.txt", conhecimentoService.obterDocumento().get().nome());
    }

    @Test
    @DisplayName("LimparDocumentoCommand deve limpar a base de conhecimento no ChatService")
    void deveExecutarLimparDocumentoCommand() {
        chatService.carregarDocumentoTexto("arquivo.txt", "texto teste");
        assertTrue(conhecimentoService.temConhecimentoCarregado());

        Comando comando = new LimparDocumentoCommand(chatService);
        comando.executar();

        assertTrue(conhecimentoService.obterDocumento().isEmpty());
    }
}
