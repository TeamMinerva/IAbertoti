package br.com.minerva.iabertoti.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SessaoChatTest {

    @Test
    void deveComecarSemMensagens() {
        SessaoChat sessao = new SessaoChat();

        assertEquals(0, sessao.getMensagens().size());
    }

    @Test
    void deveAdicionarMensagemAoProcessarPrimeiraPergunta() {
        SessaoChat sessao = new SessaoChat();

        sessao.processarPergunta("oi");

        assertEquals(1, sessao.getMensagens().size());
    }

    @Test
    void naoDeveAdicionarSegundaMensagemEnquantoProcessando() {
        SessaoChat sessao = new SessaoChat();

        sessao.processarPergunta("oi");
        sessao.processarPergunta("tudo bem?");

        assertEquals(1, sessao.getMensagens().size());
    }
}
