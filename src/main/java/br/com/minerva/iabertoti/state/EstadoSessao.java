package br.com.minerva.iabertoti.state;

import br.com.minerva.iabertoti.model.Mensagem;
import br.com.minerva.iabertoti.model.SessaoChat;

/**
 * Padrao State: define o contrato de comportamento para os diferentes
 * estados do ciclo de vida de uma sessao de chat.
 */
public interface EstadoSessao {

    void processarPergunta(SessaoChat sessao, String pergunta);

    void finalizarResposta(SessaoChat sessao, Mensagem resposta);

    String getNome();
}
