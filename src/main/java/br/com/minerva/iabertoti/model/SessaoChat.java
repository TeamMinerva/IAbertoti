package br.com.minerva.iabertoti.model;

import br.com.minerva.iabertoti.state.AguardandoPerguntaState;
import br.com.minerva.iabertoti.state.EstadoSessao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contexto do padrao State: mantem o estado atual da sessao de conversa
 * e delega o processamento da pergunta e a finalizacao da resposta para ele.
 */
public class SessaoChat {

    private EstadoSessao estadoAtual;
    private final List<Mensagem> mensagens = new ArrayList<>();

    public SessaoChat() {
        this.estadoAtual = new AguardandoPerguntaState();
    }

    public void processarPergunta(String pergunta) {
        estadoAtual.processarPergunta(this, pergunta);
    }

    public void finalizarResposta(Mensagem resposta) {
        estadoAtual.finalizarResposta(this, resposta);
    }

    public void mudarEstado(EstadoSessao novoEstado) {
        this.estadoAtual = novoEstado;
    }

    public EstadoSessao getEstadoAtual() {
        return estadoAtual;
    }

    public void adicionarMensagem(Mensagem mensagem) {
        this.mensagens.add(mensagem);
    }

    public List<Mensagem> getMensagens() {
        return Collections.unmodifiableList(mensagens);
    }
}
