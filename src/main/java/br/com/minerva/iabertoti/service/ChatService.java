package br.com.minerva.iabertoti.service;

import br.com.minerva.iabertoti.model.Autor;
import br.com.minerva.iabertoti.model.Mensagem;
import br.com.minerva.iabertoti.model.SessaoChat;
import br.com.minerva.iabertoti.observer.LogObservador;
import br.com.minerva.iabertoti.observer.ObservadorResposta;
import br.com.minerva.iabertoti.state.RespondidoState;
import br.com.minerva.iabertoti.strategy.EstrategiaResposta;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Orquestra os padrões: usa o State (via SessaoChat) para controlar o fluxo
 * da conversa, pede a resposta pra Strategy configurada, e avisa os
 * Observers registrados quando a resposta fica pronta.
 *
 * Simplificação assumida nesse MVP: uma única sessão de chat global,
 * compartilhada por todos que acessarem a aplicação (sem usuários/sessões
 * HTTP separadas). Documentado em docs/decisoes.md.
 */
@Service
public class ChatService {

    private final SessaoChat sessao = new SessaoChat();
    private final EstrategiaResposta estrategiaResposta;
    private final List<ObservadorResposta> observadores = new ArrayList<>();

    public ChatService(EstrategiaResposta estrategiaResposta) {
        this.estrategiaResposta = estrategiaResposta;
        this.observadores.add(new LogObservador());
    }

    public void enviarPergunta(String pergunta) {
        sessao.processarPergunta(pergunta);

        String textoResposta = estrategiaResposta.gerarResposta(pergunta);
        Mensagem resposta = new Mensagem(Autor.ASSISTENTE, textoResposta);
        sessao.adicionarMensagem(resposta);
        sessao.mudarEstado(new RespondidoState());

        notificarObservadores(resposta);
    }

    public void adicionarObservador(ObservadorResposta observador) {
        observadores.add(observador);
    }

    private void notificarObservadores(Mensagem resposta) {
        for (ObservadorResposta observador : observadores) {
            observador.onRespostaPronta(resposta);
        }
    }

    public List<Mensagem> getMensagens() {
        return sessao.getMensagens();
    }
}
