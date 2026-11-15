package br.com.minerva.iabertoti.controller;

import br.com.minerva.iabertoti.command.*;
import br.com.minerva.iabertoti.service.ChatService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller MVC: atua como o Invocador/Disparador dos comandos
 * encapsulados pelo padrao Command a partir das interacoes na GUI.
 */
@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/chat";
    }

    @GetMapping("/chat")
    public String exibirChat(Model model) {
        model.addAttribute("mensagens", chatService.getMensagens());
        model.addAttribute("documento", chatService.getConhecimentoService().obterDocumento().orElse(null));
        model.addAttribute("temDocumento", chatService.getConhecimentoService().temConhecimentoCarregado());
        model.addAttribute("estrategiaAtual", chatService.getNomeEstrategiaAtual());
        model.addAttribute("estrategias", chatService.getNomesEstrategiasDisponiveis());
        model.addAttribute("metricas", chatService.getMetricasObservador());
        model.addAttribute("estadoSessao", chatService.getNomeEstadoAtual());
        return "chat";
    }

    @PostMapping("/chat")
    public String enviarPergunta(@RequestParam String pergunta) {
        Comando comando = new EnviarPerguntaCommand(chatService, pergunta);
        comando.executar();
        return "redirect:/chat";
    }

    @PostMapping("/chat/documento/upload")
    public String carregarDocumento(
            @RequestParam(value = "arquivo", required = false) MultipartFile arquivo,
            @RequestParam(value = "textoDireto", required = false) String textoDireto,
            @RequestParam(value = "nomeTexto", required = false) String nomeTexto,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Comando comando;
            if (arquivo != null && !arquivo.isEmpty()) {
                comando = new CarregarDocumentoCommand(chatService, arquivo);
            } else if (textoDireto != null && !textoDireto.isBlank()) {
                comando = new CarregarDocumentoCommand(chatService, nomeTexto, textoDireto);
            } else {
                redirectAttributes.addFlashAttribute("erro", "Selecione um arquivo ou digite um texto para carregar.");
                return "redirect:/chat";
            }
            comando.executar();
            redirectAttributes.addFlashAttribute("sucesso", "Base de conhecimento atualizada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao carregar documento: " + e.getMessage());
        }
        return "redirect:/chat";
    }

    @PostMapping("/chat/documento/limpar")
    public String limparDocumento(RedirectAttributes redirectAttributes) {
        Comando comando = new LimparDocumentoCommand(chatService);
        comando.executar();
        redirectAttributes.addFlashAttribute("sucesso", "Base de conhecimento limpa.");
        return "redirect:/chat";
    }

    @PostMapping("/chat/estrategia")
    public String alternarEstrategia(@RequestParam String estrategia, RedirectAttributes redirectAttributes) {
        Comando comando = new AlternarEstrategiaCommand(chatService, estrategia);
        comando.executar();
        redirectAttributes.addFlashAttribute("sucesso", "Estrategia alterada para " + estrategia);
        return "redirect:/chat";
    }
}
