package br.com.minerva.iabertoti.state;

import br.com.minerva.iabertoti.model.SessaoChat;

public interface EstadoSessao {
    void processarPergunta(SessaoChat sessao, String pergunta);
}
