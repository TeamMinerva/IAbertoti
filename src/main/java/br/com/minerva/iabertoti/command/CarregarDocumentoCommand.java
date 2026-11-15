package br.com.minerva.iabertoti.command;

import br.com.minerva.iabertoti.service.ChatService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Comando concreto: encapsula a acao de carregar um documento ou arquivo
 * de texto como base de conhecimento para a sessao do Knowledge Assistant.
 */
public class CarregarDocumentoCommand implements Comando {

    private final ChatService chatService;
    private final MultipartFile arquivo;
    private final String textoDireto;
    private final String nomeDocumento;

    public CarregarDocumentoCommand(ChatService chatService, MultipartFile arquivo) {
        this.chatService = chatService;
        this.arquivo = arquivo;
        this.textoDireto = null;
        this.nomeDocumento = null;
    }

    public CarregarDocumentoCommand(ChatService chatService, String nomeDocumento, String textoDireto) {
        this.chatService = chatService;
        this.arquivo = null;
        this.nomeDocumento = nomeDocumento;
        this.textoDireto = textoDireto;
    }

    @Override
    public void executar() {
        try {
            if (arquivo != null && !arquivo.isEmpty()) {
                chatService.carregarDocumentoArquivo(arquivo);
            } else if (textoDireto != null && !textoDireto.isBlank()) {
                chatService.carregarDocumentoTexto(nomeDocumento, textoDireto);
            }
        } catch (IOException e) {
            throw new RuntimeException("Falha ao carregar o documento: " + e.getMessage(), e);
        }
    }
}
