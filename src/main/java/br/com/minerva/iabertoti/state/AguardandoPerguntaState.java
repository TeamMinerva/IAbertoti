package br.com.minerva.iabertoti.state;

import br.com.minerva.iabertoti.model.Autor;
import br.com.minerva.iabertoti.model.Mensagem;
import br.com.minerva.iabertoti.model.SessaoChat;

public class AguardandoPerguntaState implements EstadoSessao {

    @Override
    public void processarPergunta(SessaoChat sessao, String pergunta) {
        sessao.adicionarMensagem(new Mensagem(Autor.USUARIO, pergunta));
        sessao.mudarEstado(new ProcessandoState());
    }
}
