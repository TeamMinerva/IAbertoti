package br.com.minerva.iabertoti.strategy;

import br.com.minerva.iabertoti.service.ConhecimentoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SpringAiKnowledgeStrategyTest {

    @Test
    @DisplayName("Deve retornar mensagem explicativa amigavel caso o Ollama nao esteja ativo")
    void deveTratarErroQuandoOllamaOffline() {
        ChatClient chatClient = Mockito.mock(ChatClient.class);
        ConhecimentoService conhecimentoService = new ConhecimentoService();

        when(chatClient.prompt(any(org.springframework.ai.chat.prompt.Prompt.class)))
                .thenThrow(new RuntimeException("Connection refused: localhost/127.0.0.1:11434"));

        SpringAiKnowledgeStrategy strategy = new SpringAiKnowledgeStrategy(chatClient, conhecimentoService);
        String resposta = strategy.gerarResposta("Qualquer pergunta");

        assertTrue(resposta.contains("Erro de conexao com o modelo local Ollama"));
        assertTrue(resposta.contains("11434"));
    }
}
