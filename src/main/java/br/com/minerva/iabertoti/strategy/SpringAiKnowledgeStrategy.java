package br.com.minerva.iabertoti.strategy;

import br.com.minerva.iabertoti.service.ConhecimentoService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Estrategia concreta de geracao de resposta utilizando Spring AI e modelos open-source (Ollama).
 * Atua como Knowledge Assistant: injeta a base de conhecimento no prompt do sistema
 * e invoca o LLM local para formular a resposta.
 */
@Component("springAiKnowledgeStrategy")
@Primary
public class SpringAiKnowledgeStrategy implements EstrategiaResposta {

    private final ChatClient chatClient;
    private final ConhecimentoService conhecimentoService;

    public SpringAiKnowledgeStrategy(ChatClient chatClient, ConhecimentoService conhecimentoService) {
        this.chatClient = chatClient;
        this.conhecimentoService = conhecimentoService;
    }

    @Override
    public String gerarResposta(String pergunta) {
        try {
            Prompt prompt = conhecimentoService.montarPrompt(pergunta);
            String resposta = chatClient.prompt(prompt)
                    .call()
                    .content();

            return (resposta != null && !resposta.isBlank())
                    ? resposta.trim()
                    : "O modelo nao gerou conteudo para esta pergunta.";
        } catch (Exception e) {
            return "[Erro de conexao com o modelo local Ollama]\n"
                    + "Certifique-se de que o Ollama esta rodando na porta 11434 (`ollama run llama3.1` ou modelo configurado). "
                    + "Voce tambem pode alternar para a EcoStrategy na interface caso queira testar sem a LLM ativa.\n"
                    + "Detalhes: " + e.getMessage();
        }
    }
}
