package br.com.minerva.iabertoti.strategy;

/**
 * Padrão Strategy: define o contrato para as diferentes formas de gerar
 * uma resposta a partir da pergunta do usuário. Permite trocar o "cérebro"
 * que gera a resposta (ex.: uma versão simples agora, um modelo de IA real
 * depois) sem alterar quem usa essa estratégia.
 */
public interface EstrategiaResposta {

    String gerarResposta(String pergunta);
}
