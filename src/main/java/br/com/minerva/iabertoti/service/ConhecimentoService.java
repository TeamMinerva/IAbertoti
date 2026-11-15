package br.com.minerva.iabertoti.service;

import br.com.minerva.iabertoti.model.DocumentoConhecimento;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servico responsavel pela gestao da base de conhecimento do Knowledge Assistant.
 * Armazena o documento atual (via texto ou upload de arquivo) e gera os prompts
 * enriquecidos com contexto (RAG simples) para alimentacao da IA.
 */
@Service
public class ConhecimentoService {

    private static final String SYSTEM_TEMPLATE_COM_DOCUMENTO = """
            Voce e um assistente de conhecimento especializado (Knowledge Assistant).
            Sua missao e responder a pergunta do usuario baseando-se estritamente nas informacoes
            contidas no documento fornecido no contexto abaixo.

            Diretrizes:
            1. Utilize unicamente os fatos e dados presentes no documento.
            2. Se a resposta nao puder ser respondida com base no documento, informe educadamente
               que o documento fornecido nao contem essa informacao.
            3. Seja objetivo, claro e conciso.

            --- DOCUMENTO DE CONHECIMENTO ---
            {documento}
            --------------------------------
            """;

    private static final String SYSTEM_TEMPLATE_SEM_DOCUMENTO = """
            Voce e um assistente de IA. Atualmente nenhum documento de conhecimento foi carregado
            na aplicacao. Avise o usuario que ele pode enviar um arquivo de texto (.txt ou .md) para
            que voce atue como Knowledge Assistant respondendo duvidas sobre ele.
            """;

    private DocumentoConhecimento documentoAtual;

    /**
     * Carrega uma base de conhecimento a partir de um texto direto.
     */
    public DocumentoConhecimento carregarTexto(String nome, String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("O conteudo do texto nao pode ser vazio.");
        }
        this.documentoAtual = new DocumentoConhecimento(nome, texto.trim());
        return this.documentoAtual;
    }

    /**
     * Carrega uma base de conhecimento a partir de um arquivo enviado (ex: .txt ou .md).
     */
    public DocumentoConhecimento carregarArquivo(MultipartFile arquivo) throws IOException {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("O arquivo enviado esta vazio ou nao foi selecionado.");
        }

        String nomeOriginal = arquivo.getOriginalFilename();
        String nome = (nomeOriginal != null && !nomeOriginal.isBlank()) ? nomeOriginal : "documento.txt";
        String conteudo = new String(arquivo.getBytes(), StandardCharsets.UTF_8).trim();

        if (conteudo.isBlank()) {
            throw new IllegalArgumentException("O arquivo enviado nao possui conteudo legivel.");
        }

        this.documentoAtual = new DocumentoConhecimento(nome, conteudo);
        return this.documentoAtual;
    }

    /**
     * Retorna o documento de conhecimento atualmente ativo, se houver.
     */
    public Optional<DocumentoConhecimento> obterDocumento() {
        return Optional.ofNullable(documentoAtual);
    }

    /**
     * Retorna o texto do documento atual ou vazio caso nenhum esteja ativo.
     */
    public String obterConteudo() {
        return documentoAtual != null ? documentoAtual.conteudo() : "";
    }

    /**
     * Informa se ha uma base de conhecimento carregada na sessao.
     */
    public boolean temConhecimentoCarregado() {
        return documentoAtual != null && !documentoAtual.conteudo().isBlank();
    }

    /**
     * Limpa o documento atual da memoria.
     */
    public void limpar() {
        this.documentoAtual = null;
    }

    /**
     * Monta o objeto Prompt do Spring AI combinando as instrucoes de sistema
     * (com o documento de contexto injetado) e a pergunta do usuario.
     */
    public Prompt montarPrompt(String pergunta) {
        if (pergunta == null || pergunta.isBlank()) {
            throw new IllegalArgumentException("A pergunta nao pode ser nula ou vazia.");
        }

        Message systemMessage;
        if (temConhecimentoCarregado()) {
            SystemPromptTemplate template = new SystemPromptTemplate(SYSTEM_TEMPLATE_COM_DOCUMENTO);
            systemMessage = template.createMessage(Map.of("documento", obterConteudo()));
        } else {
            SystemPromptTemplate template = new SystemPromptTemplate(SYSTEM_TEMPLATE_SEM_DOCUMENTO);
            systemMessage = template.createMessage();
        }

        UserMessage userMessage = new UserMessage(pergunta);
        return new Prompt(List.of(systemMessage, userMessage));
    }
}
