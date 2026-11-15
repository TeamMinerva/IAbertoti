package br.com.minerva.iabertoti.observer;

import br.com.minerva.iabertoti.model.Mensagem;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Observador de telemetria e metricas: reage ao evento onRespostaPronta
 * contabilizando o total de respostas geradas, caracteres processados
 * e o timestamp da ultima interacao.
 */
@Component
public class MetricasObservador implements ObservadorResposta {

    private int totalRespostas = 0;
    private long totalCaracteres = 0;
    private LocalDateTime ultimaRespostaEm;

    @Override
    public synchronized void onRespostaPronta(Mensagem resposta) {
        totalRespostas++;
        if (resposta != null && resposta.conteudo() != null) {
            totalCaracteres += resposta.conteudo().length();
        }
        ultimaRespostaEm = LocalDateTime.now();
    }

    public synchronized int getTotalRespostas() {
        return totalRespostas;
    }

    public synchronized long getTotalCaracteres() {
        return totalCaracteres;
    }

    public synchronized LocalDateTime getUltimaRespostaEm() {
        return ultimaRespostaEm;
    }

    public synchronized double getMediaCaracteres() {
        return totalRespostas > 0 ? (double) totalCaracteres / totalRespostas : 0.0;
    }
}
