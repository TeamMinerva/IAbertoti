package br.com.minerva.iabertoti.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracao de infraestrutura do Spring AI.
 * Disponibiliza o ChatClient configurado com o ChatClient.Builder fornecido
 * pelo starter do Ollama.
 */
@Configuration
public class SpringAiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}
