package br.com.minerva.iabertoti.command;

import br.com.minerva.iabertoti.service.ChatService;

/**
 * Comando concreto: encapsula a acao de limpar o documento atual
 * da base de conhecimento da aplicacao.
 */
public class LimparDocumentoCommand implements Comando {

    private final ChatService chatService;

    public LimparDocumentoCommand(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public void executar() {
        chatService.limparDocumento();
    }
}
