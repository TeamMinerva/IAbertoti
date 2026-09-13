package br.com.minerva.iabertoti.model;

import br.com.minerva.iabertoti.state.AguardandoPerguntaState;
import br.com.minerva.iabertoti.state.EstadoSessao;

import java.util.ArrayList;
import java.util.List;

public class SessaoChat {

    private EstadoSessao estadoAtual;
    private List<Mensagem> mensagens = new ArrayList<>();


    public SessaoChat(){
        this.estadoAtual = new AguardandoPerguntaState();
    }

    public void processarPergunta(String pergunta){
        estadoAtual.processarPergunta(this, pergunta);
    }

    public void mudarEstado(EstadoSessao novoEstado){
        this.estadoAtual = novoEstado;
    }

    public void adicionarMensagem(Mensagem mensagem){
        this.mensagens.add(mensagem);
    }

    public List<Mensagem> getMensagens(){
        return mensagens;
    }

}
