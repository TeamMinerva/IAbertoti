package br.com.minerva.iabertoti.state;

import br.com.minerva.iabertoti.model.Mensagem;
import br.com.minerva.iabertoti.model.SessaoChat;

/**
 * Estado onde a sessao esta aguardando a resposta da IA.
 * Novas perguntas sao bloqueadas ate que a resposta anterior seja finalizada.
 */
public class ProcessandoState implements EstadoSessao {

    @Override
    public void processarPergunta(SessaoChat sessao, String pergunta) {
        System.out.println("[State] Aguarde, ainda estou processando a pergunta anterior.");
    }

    @Override
    public void finalizarResposta(SessaoChat sessao, Mensagem resposta) {
        sessao.adicionarMensagem(resposta);
        sessao.mudarEstado(new RespondidoState());
    }

    @Override
    public String getNome() {
        return "Processando";
    }
}
