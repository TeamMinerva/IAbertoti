package br.com.minerva.iabertoti.observer;

import br.com.minerva.iabertoti.model.Mensagem;

/**
 * Padrão Observer: quem implementar essa interface e se registrar no
 * ChatService é avisado automaticamente quando uma nova resposta fica
 * pronta, sem precisar ficar checando o tempo todo.
 */
public interface ObservadorResposta {

    void onRespostaPronta(Mensagem resposta);
}
