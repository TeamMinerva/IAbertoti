package br.com.minerva.iabertoti.service;

import br.com.minerva.iabertoti.model.Autor;
import br.com.minerva.iabertoti.model.DocumentoConhecimento;
import br.com.minerva.iabertoti.model.Mensagem;
import br.com.minerva.iabertoti.model.SessaoChat;
import br.com.minerva.iabertoti.observer.LogObservador;
import br.com.minerva.iabertoti.observer.MetricasObservador;
import br.com.minerva.iabertoti.observer.ObservadorResposta;
import br.com.minerva.iabertoti.strategy.EstrategiaResposta;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * Orquestrador da aplicacao:
 * - State: gerencia o fluxo de estados via SessaoChat (Aguardando -> Processando -> Respondido);
 * - Strategy: delega a geracao da resposta a EstrategiaResposta ativa (Spring AI Ollama ou Eco Mock);
 * - Observer: notifica observadores (LogObservador, MetricasObservador) quando a resposta esta pronta;
 * - Conhecimento: interage com o ConhecimentoService para manter os documentos que alimentam a IA.
 */
@Service
public class ChatService {

    private final SessaoChat sessao = new SessaoChat();
    private final ConhecimentoService conhecimentoService;
    private final MetricasObservador metricasObservador;
    private final List<ObservadorResposta> observadores = new ArrayList<>();
    private final Map<String, EstrategiaResposta> estrategiasDisponiveis = new LinkedHashMap<>();
    private EstrategiaResposta estrategiaAtual;

    public ChatService(
            EstrategiaResposta estrategiaPadrao,
            ConhecimentoService conhecimentoService,
            MetricasObservador metricasObservador,
            List<EstrategiaResposta> todasEstrategias
    ) {
        this.conhecimentoService = conhecimentoService;
        this.metricasObservador = metricasObservador;

        for (EstrategiaResposta est : todasEstrategias) {
            this.estrategiasDisponiveis.put(est.getClass().getSimpleName(), est);
        }
        this.estrategiaAtual = estrategiaPadrao;

        this.observadores.add(new LogObservador());
        this.observadores.add(metricasObservador);
    }

    public void enviarPergunta(String pergunta) {
        if (pergunta == null || pergunta.isBlank()) {
            return;
        }

        // 1. Padrao State: transiciona para ProcessandoState e armazena mensagem do usuario
        sessao.processarPergunta(pergunta);

        // 2. Padrao Strategy: gera a resposta com o algoritmo/modelo ativo
        String textoResposta = estrategiaAtual.gerarResposta(pergunta);
        Mensagem resposta = new Mensagem(Autor.ASSISTENTE, textoResposta);

        // 3. Padrao State: finaliza e transiciona para RespondidoState
        sessao.finalizarResposta(resposta);

        // 4. Padrao Observer: notifica todos os observadores inscritos
        notificarObservadores(resposta);
    }

    public DocumentoConhecimento carregarDocumentoArquivo(MultipartFile arquivo) throws IOException {
        DocumentoConhecimento doc = conhecimentoService.carregarArquivo(arquivo);
        Mensagem aviso = new Mensagem(
                Autor.ASSISTENTE,
                "Documento '" + doc.nome() + "' (" + doc.tamanhoBytes() + " bytes) carregado com sucesso na base de conhecimento! Agora voce pode fazer perguntas sobre ele."
        );
        sessao.adicionarMensagem(aviso);
        return doc;
    }

    public DocumentoConhecimento carregarDocumentoTexto(String nome, String texto) {
        DocumentoConhecimento doc = conhecimentoService.carregarTexto(nome, texto);
        Mensagem aviso = new Mensagem(
                Autor.ASSISTENTE,
                "Texto '" + doc.nome() + "' carregado com sucesso na base de conhecimento! Agora voce pode fazer perguntas sobre ele."
        );
        sessao.adicionarMensagem(aviso);
        return doc;
    }

    public void limparDocumento() {
        conhecimentoService.limpar();
        Mensagem aviso = new Mensagem(
                Autor.ASSISTENTE,
                "A base de conhecimento foi limpa. Nao ha nenhum documento ativo no momento."
        );
        sessao.adicionarMensagem(aviso);
    }

    public void selecionarEstrategia(String nomeEstrategia) {
        if (estrategiasDisponiveis.containsKey(nomeEstrategia)) {
            this.estrategiaAtual = estrategiasDisponiveis.get(nomeEstrategia);
            sessao.adicionarMensagem(new Mensagem(
                    Autor.ASSISTENTE,
                    "Estrategia de resposta alterada para: " + nomeEstrategia
            ));
        }
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

    public SessaoChat getSessao() {
        return sessao;
    }

    public ConhecimentoService getConhecimentoService() {
        return conhecimentoService;
    }

    public MetricasObservador getMetricasObservador() {
        return metricasObservador;
    }

    public String getNomeEstrategiaAtual() {
        return estrategiaAtual != null ? estrategiaAtual.getClass().getSimpleName() : "Nenhuma";
    }

    public Set<String> getNomesEstrategiasDisponiveis() {
        return Collections.unmodifiableSet(estrategiasDisponiveis.keySet());
    }

    public String getNomeEstadoAtual() {
        return sessao.getEstadoAtual().getNome();
    }
}
