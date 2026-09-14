package br.com.minerva.iabertoti.observer;

import br.com.minerva.iabertoti.model.Mensagem;

/**
 * Implementação simples de ObservadorResposta: só registra no console
 * quando uma resposta fica pronta. Prova o funcionamento do padrão
 * Observer sem depender de nada externo (ex.: WebSocket, e-mail, etc.).
 */
public class LogObservador implements ObservadorResposta {

    @Override
    public void onRespostaPronta(Mensagem resposta) {
        System.out.println("[Observer] Resposta pronta: " + resposta.conteudo());
    }
}
