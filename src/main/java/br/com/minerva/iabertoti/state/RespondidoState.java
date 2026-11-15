package br.com.minerva.iabertoti.state;

import br.com.minerva.iabertoti.model.Mensagem;
import br.com.minerva.iabertoti.model.SessaoChat;

/**
 * Estado onde a ultima resposta ja foi concluida e entregue.
 * Ao receber uma nova pergunta, transiciona de volta para AguardandoPerguntaState.
 */
public class RespondidoState implements EstadoSessao {

    @Override
    public void processarPergunta(SessaoChat sessao, String pergunta) {
        sessao.mudarEstado(new AguardandoPerguntaState());
        sessao.processarPergunta(pergunta);
    }

    @Override
    public void finalizarResposta(SessaoChat sessao, Mensagem resposta) {
        // A resposta ja esta finalizada
    }

    @Override
    public String getNome() {
        return "Respondido";
    }
}
