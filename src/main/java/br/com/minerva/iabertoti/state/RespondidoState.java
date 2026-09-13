package br.com.minerva.iabertoti.state;

import br.com.minerva.iabertoti.model.SessaoChat;

public class RespondidoState implements EstadoSessao {

    @Override
    public void processarPergunta(SessaoChat sessao, String pergunta) {
        // A resposta anterior já ficou pronta — volta a aceitar perguntas normalmente.
        sessao.mudarEstado(new AguardandoPerguntaState());
        sessao.processarPergunta(pergunta);
    }
}
