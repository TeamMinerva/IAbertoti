package br.com.minerva.iabertoti.strategy;

import org.springframework.stereotype.Component;

/**
 * Implementação mínima do Strategy: não usa nenhum modelo de IA de verdade
 * ainda, só devolve uma resposta simples a partir da pergunta. Serve pra
 * provar que a arquitetura funciona (Command -> State -> Strategy ->
 * Observer) sem depender de infraestrutura externa (Ollama) na entrega.
 */
@Component
public class EcoStrategy implements EstrategiaResposta {

    @Override
    public String gerarResposta(String pergunta) {
        return "Você perguntou: \"" + pergunta + "\". Ainda não tenho um modelo de IA plugado, "
                + "mas a arquitetura já está pronta pra receber um.";
    }
}
