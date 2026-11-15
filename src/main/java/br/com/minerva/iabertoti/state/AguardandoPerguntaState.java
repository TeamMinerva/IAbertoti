package br.com.minerva.iabertoti.state;

import br.com.minerva.iabertoti.model.Autor;
import br.com.minerva.iabertoti.model.Mensagem;
import br.com.minerva.iabertoti.model.SessaoChat;

/**
 * Estado onde a sessao esta pronta para receber novas perguntas.
 */
public class AguardandoPerguntaState implements EstadoSessao {

    @Override
    public void processarPergunta(SessaoChat sessao, String pergunta) {
        sessao.adicionarMensagem(new Mensagem(Autor.USUARIO, pergunta));
        sessao.mudarEstado(new ProcessandoState());
    }

    @Override
    public void finalizarResposta(SessaoChat sessao, Mensagem resposta) {
        // Nenhuma resposta em processamento para ser finalizada neste estado
    }

    @Override
    public String getNome() {
        return "Aguardando Pergunta";
    }
}
