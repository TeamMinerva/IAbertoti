package br.com.minerva.iabertoti.state;

import br.com.minerva.iabertoti.model.SessaoChat;

public class ProcessandoState implements EstadoSessao {

    @Override
    public void processarPergunta(SessaoChat sessao, String pergunta) {
        // Já está processando a pergunta anterior — ignora novas tentativas por enquanto.
        System.out.println("Aguarde, ainda estou processando a pergunta anterior.");
    }
}
