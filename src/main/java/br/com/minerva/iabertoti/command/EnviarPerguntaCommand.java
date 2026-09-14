package br.com.minerva.iabertoti.command;

import br.com.minerva.iabertoti.service.ChatService;

/**
 * Comando concreto: encapsula a ação "enviar uma pergunta", que o
 * ChatController monta a partir do que chega da GUI e apenas executa,
 * sem conhecer os detalhes de como a pergunta é processada.
 */
public class EnviarPerguntaCommand implements Comando {

    private final ChatService chatService;
    private final String pergunta;

    public EnviarPerguntaCommand(ChatService chatService, String pergunta) {
        this.chatService = chatService;
        this.pergunta = pergunta;
    }

    @Override
    public void executar() {
        chatService.enviarPergunta(pergunta);
    }
}
