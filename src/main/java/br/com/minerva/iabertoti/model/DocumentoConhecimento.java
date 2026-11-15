package br.com.minerva.iabertoti.model;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * Representa um documento ou fonte de texto carregado como base
 * de conhecimento para o Knowledge Assistant.
 */
public record DocumentoConhecimento(
        String nome,
        String conteudo,
        long tamanhoBytes,
        LocalDateTime dataCarregamento
) {

    public DocumentoConhecimento(String nome, String conteudo) {
        this(
                nome != null && !nome.isBlank() ? nome : "documento_sem_nome.txt",
                conteudo != null ? conteudo : "",
                conteudo != null ? conteudo.getBytes(StandardCharsets.UTF_8).length : 0L,
                LocalDateTime.now()
        );
    }
}
