package br.com.minerva.iabertoti.command;

import br.com.minerva.iabertoti.service.ChatService;

/**
 * Comando concreto: encapsula a acao de alternar a Strategy utilizada
 * para geracao de resposta (ex: Spring AI Ollama vs EcoStrategy).
 */
public class AlternarEstrategiaCommand implements Comando {

    private final ChatService chatService;
    private final String nomeEstrategia;

    public AlternarEstrategiaCommand(ChatService chatService, String nomeEstrategia) {
        this.chatService = chatService;
        this.nomeEstrategia = nomeEstrategia;
    }

    @Override
    public void executar() {
        chatService.selecionarEstrategia(nomeEstrategia);
    }
}
