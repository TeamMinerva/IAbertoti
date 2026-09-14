package br.com.minerva.iabertoti.command;

/**
 * Padrão Command: representa uma ação disparada pela interface como um
 * objeto próprio, desacoplando o controller (que recebe o clique/submit)
 * da lógica que a ação executa.
 */
public interface Comando {

    void executar();
}
