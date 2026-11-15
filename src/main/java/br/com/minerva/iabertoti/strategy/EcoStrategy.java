package br.com.minerva.iabertoti.strategy;

import org.springframework.stereotype.Component;

/**
 * Implementacao simulada (mock) do Strategy: util para testes unitarios,
 * apresentacoes e desenvolvimento offline sem depender da inicializacao do Ollama.
 */
@Component("ecoStrategy")
public class EcoStrategy implements EstrategiaResposta {

    @Override
    public String gerarResposta(String pergunta) {
        return "Voce perguntou: \"" + pergunta + "\". [Resposta gerada pela EcoStrategy / Mock de Desenvolvimento]";
    }
}
