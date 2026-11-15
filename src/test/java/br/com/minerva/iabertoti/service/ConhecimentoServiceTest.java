package br.com.minerva.iabertoti.service;

import br.com.minerva.iabertoti.model.DocumentoConhecimento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ConhecimentoServiceTest {

    private ConhecimentoService conhecimentoService;

    @BeforeEach
    void setUp() {
        conhecimentoService = new ConhecimentoService();
    }

    @Test
    @DisplayName("Deve comecar sem nenhum documento carregado")
    void deveComecarSemDocumento() {
        assertFalse(conhecimentoService.temConhecimentoCarregado());
        assertTrue(conhecimentoService.obterDocumento().isEmpty());
        assertEquals("", conhecimentoService.obterConteudo());
    }

    @Test
    @DisplayName("Deve carregar base de conhecimento a partir de texto direto")
    void deveCarregarTextoComSucesso() {
        DocumentoConhecimento doc = conhecimentoService.carregarTexto("manual.txt", "Este e o conteudo do manual.");

        assertTrue(conhecimentoService.temConhecimentoCarregado());
        assertEquals("manual.txt", doc.nome());
        assertEquals("Este e o conteudo do manual.", doc.conteudo());
        assertTrue(doc.tamanhoBytes() > 0);
        assertNotNull(doc.dataCarregamento());

        Optional<DocumentoConhecimento> obtido = conhecimentoService.obterDocumento();
        assertTrue(obtido.isPresent());
        assertEquals("manual.txt", obtido.get().nome());
    }

    @Test
    @DisplayName("Deve carregar base de conhecimento a partir de arquivo MultipartFile")
    void deveCarregarArquivoComSucesso() throws IOException {
        String texto = "Conteudo de politicas da empresa para Knowledge Assistant.";
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "politicas.txt",
                "text/plain",
                texto.getBytes(StandardCharsets.UTF_8)
        );

        DocumentoConhecimento doc = conhecimentoService.carregarArquivo(arquivo);

        assertTrue(conhecimentoService.temConhecimentoCarregado());
        assertEquals("politicas.txt", doc.nome());
        assertEquals(texto, doc.conteudo());
        assertEquals(texto, conhecimentoService.obterConteudo());
    }

    @Test
    @DisplayName("Deve lancar excecao ao carregar texto nulo ou vazio")
    void deveLancarExcecaoAoCarregarTextoVazio() {
        assertThrows(IllegalArgumentException.class, () -> conhecimentoService.carregarTexto("teste.txt", null));
        assertThrows(IllegalArgumentException.class, () -> conhecimentoService.carregarTexto("teste.txt", "   "));
    }

    @Test
    @DisplayName("Deve lancar excecao ao carregar arquivo nulo ou vazio")
    void deveLancarExcecaoAoCarregarArquivoVazio() {
        assertThrows(IllegalArgumentException.class, () -> conhecimentoService.carregarArquivo(null));

        MockMultipartFile arquivoVazio = new MockMultipartFile("arquivo", "vazio.txt", "text/plain", new byte[0]);
        assertThrows(IllegalArgumentException.class, () -> conhecimentoService.carregarArquivo(arquivoVazio));
    }

    @Test
    @DisplayName("Deve limpar documento carregado com sucesso")
    void deveLimparDocumento() {
        conhecimentoService.carregarTexto("nota.txt", "Texto temporario");
        assertTrue(conhecimentoService.temConhecimentoCarregado());

        conhecimentoService.limpar();

        assertFalse(conhecimentoService.temConhecimentoCarregado());
        assertTrue(conhecimentoService.obterDocumento().isEmpty());
    }

    @Test
    @DisplayName("Deve montar Prompt do Spring AI com contexto quando ha documento carregado")
    void deveMontarPromptComDocumento() {
        conhecimentoService.carregarTexto("faq.txt", "O horario de atendimento e das 8h as 18h.");

        Prompt prompt = conhecimentoService.montarPrompt("Qual o horario de atendimento?");

        assertNotNull(prompt);
        assertEquals(2, prompt.getInstructions().size());

        String systemMessageText = prompt.getInstructions().get(0).getText();
        String userMessageText = prompt.getInstructions().get(1).getText();

        assertTrue(systemMessageText.contains("Knowledge Assistant"));
        assertTrue(systemMessageText.contains("O horario de atendimento e das 8h as 18h."));
        assertEquals("Qual o horario de atendimento?", userMessageText);
    }

    @Test
    @DisplayName("Deve montar Prompt informativo quando nao ha documento carregado")
    void deveMontarPromptSemDocumento() {
        Prompt prompt = conhecimentoService.montarPrompt("Como funciona o sistema?");

        assertNotNull(prompt);
        assertEquals(2, prompt.getInstructions().size());

        String systemMessageText = prompt.getInstructions().get(0).getText();
        assertTrue(systemMessageText.contains("nenhum documento de conhecimento foi carregado"));
    }
}
