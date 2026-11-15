package br.com.minerva.iabertoti.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SessaoChatTest {

    @Test
    @DisplayName("Deve comecar sem mensagens e no estado Aguardando Pergunta")
    void deveComecarSemMensagens() {
        SessaoChat sessao = new SessaoChat();

        assertEquals(0, sessao.getMensagens().size());
        assertEquals("Aguardando Pergunta", sessao.getEstadoAtual().getNome());
    }

    @Test
    @DisplayName("Deve adicionar mensagem e mudar para Processando ao receber primeira pergunta")
    void deveAdicionarMensagemAoProcessarPrimeiraPergunta() {
        SessaoChat sessao = new SessaoChat();

        sessao.processarPergunta("oi");

        assertEquals(1, sessao.getMensagens().size());
        assertEquals("Processando", sessao.getEstadoAtual().getNome());
    }

    @Test
    @DisplayName("Nao deve aceitar segunda mensagem enquanto estiver no estado Processando")
    void naoDeveAdicionarSegundaMensagemEnquantoProcessando() {
        SessaoChat sessao = new SessaoChat();

        sessao.processarPergunta("oi");
        sessao.processarPergunta("tudo bem?");

        assertEquals(1, sessao.getMensagens().size());
        assertEquals("Processando", sessao.getEstadoAtual().getNome());
    }

    @Test
    @DisplayName("Deve adicionar resposta e mudar para Respondido ao finalizar resposta")
    void deveMudarParaRespondidoAoFinalizarResposta() {
        SessaoChat sessao = new SessaoChat();
        sessao.processarPergunta("Qual o resumo?");

        sessao.finalizarResposta(new Mensagem(Autor.ASSISTENTE, "Aqui esta o resumo."));

        assertEquals(2, sessao.getMensagens().size());
        assertEquals("Respondido", sessao.getEstadoAtual().getNome());
    }

    @Test
    @DisplayName("Deve voltar a aceitar perguntas normalmente apos estar no estado Respondido")
    void deveAceitarNovaPerguntaAposRespondido() {
        SessaoChat sessao = new SessaoChat();
        sessao.processarPergunta("Pergunta 1");
        sessao.finalizarResposta(new Mensagem(Autor.ASSISTENTE, "Resposta 1"));

        sessao.processarPergunta("Pergunta 2");

        assertEquals(3, sessao.getMensagens().size());
        assertEquals("Processando", sessao.getEstadoAtual().getNome());
    }
}
