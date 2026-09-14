package br.com.minerva.iabertoti.controller;

import br.com.minerva.iabertoti.command.Comando;
import br.com.minerva.iabertoti.command.EnviarPerguntaCommand;
import br.com.minerva.iabertoti.service.ChatService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chat")
    public String exibirChat(Model model) {
        model.addAttribute("mensagens", chatService.getMensagens());
        return "chat";
    }

    @PostMapping("/chat")
    public String enviarPergunta(@RequestParam String pergunta) {
        Comando comando = new EnviarPerguntaCommand(chatService, pergunta);
        comando.executar();
        return "redirect:/chat";
    }
}
