package br.com.minerva.iabertoti;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class IabertotiApplicationTests {

	@Autowired(required = false)
	private ChatClient chatClient;

	@Autowired(required = false)
	private ChatClient.Builder chatClientBuilder;

	@Test
	@DisplayName("Deve carregar o contexto da aplicacao e disponibilizar os beans do Spring AI")
	void contextLoads() {
		assertNotNull(chatClient, "O bean ChatClient configurado em SpringAiConfig deve estar disponivel no contexto");
		assertNotNull(chatClientBuilder, "O ChatClient.Builder fornecido pelo starter do Ollama deve estar presente");
	}

}

