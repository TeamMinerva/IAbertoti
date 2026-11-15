# Diagramas UML da Arquitetura - IAbertoti

Este diretório contém os diagramas de arquitetura e modelagem formal dos **5 Padrões de Projeto (GoF)** exigidos na disciplina de Engenharia de Software III (Prof. Bertoti).

Os diagramas estão disponíveis tanto no formato nativo **PlantUML (`.puml`)** para edição e renderização com a ferramenta oficial, quanto em **Mermaid**, renderizado automaticamente pela interface do GitHub.

---

## 1. Visão Geral da Arquitetura

Arquivo PlantUML: [`arquitetura-geral.puml`](./arquitetura-geral.puml)

```mermaid
flowchart TD
    subgraph GUI ["GUI (Composite)"]
        ChatHTML["chat.html (Raiz)"]
        PainelHTML["painel-conhecimento.html (Galho)"]
        ListaHTML["lista-mensagens.html (Galho)"]
        MsgHTML["mensagem.html (Folha)"]
        ChatHTML --> PainelHTML
        ChatHTML --> ListaHTML
        ListaHTML --> MsgHTML
    end

    subgraph Controller ["Camada Web MVC"]
        ChatController["ChatController\n(Invoker do Command)"]
    end

    subgraph Commands ["Padrão Command"]
        Comando["<<interface>>\nComando"]
        CmdPergunta["EnviarPerguntaCommand"]
        CmdDoc["CarregarDocumentoCommand"]
        CmdLimpar["LimparDocumentoCommand"]
        CmdEstrategia["AlternarEstrategiaCommand"]
        Comando <|.. CmdPergunta
        Comando <|.. CmdDoc
        Comando <|.. CmdLimpar
        Comando <|.. CmdEstrategia
    end

    subgraph Service ["Camada de Serviços & Orquestração"]
        ChatService["ChatService\n(Context & Subject)"]
        ConhecimentoService["ConhecimentoService\n(Gestão de RAG)"]
    end

    subgraph StatePattern ["Padrão State (Ciclo de Vida)"]
        SessaoChat["SessaoChat (Context)"]
        EstadoSessao["<<interface>>\nEstadoSessao"]
        AguardandoState["AguardandoPerguntaState"]
        ProcessandoState["ProcessandoState"]
        RespondidoState["RespondidoState"]
        SessaoChat --> EstadoSessao
        EstadoSessao <|.. AguardandoState
        EstadoSessao <|.. ProcessandoState
        EstadoSessao <|.. RespondidoState
    end

    subgraph StrategyPattern ["Padrão Strategy (Geração de Resposta)"]
        EstrategiaResposta["<<interface>>\nEstrategiaResposta"]
        SpringAiStrat["SpringAiKnowledgeStrategy\n(@Primary)"]
        EcoStrat["EcoStrategy\n(Mock / Dev)"]
        EstrategiaResposta <|.. SpringAiStrat
        EstrategiaResposta <|.. EcoStrat
    end

    subgraph ObserverPattern ["Padrão Observer (Eventos)"]
        ObservadorResposta["<<interface>>\nObservadorResposta"]
        LogObs["LogObservador"]
        MetricasObs["MetricasObservador"]
        ObservadorResposta <|.. LogObs
        ObservadorResposta <|.. MetricasObs
    end

    subgraph LLM ["Spring AI + Modelo Aberto"]
        ChatClient["ChatClient"]
        OllamaLocal["Ollama Server (Local:11434)\nModelo: llama3.1 / llama3.2"]
        ChatClient --> OllamaLocal
    end

    GUI -->|HTTP POST| ChatController
    ChatController -->|instancia e executa| Commands
    Commands -->|chama lógica no receiver| ChatService
    ChatService -->|controla ciclo| SessaoChat
    ChatService -->|delega resposta| StrategyPattern
    ChatService -->|notifica resposta| ObserverPattern
    ChatService -->|gerencia base| ConhecimentoService
    SpringAiStrat -->|injeta contexto do documento| ConhecimentoService
    SpringAiStrat -->|invoca inferência| ChatClient
```

---

## 2. Padrão State

Arquivo PlantUML: [`state.puml`](./state.puml)

### Diagrama de Estados (Ciclo de Vida da Sessão):
```mermaid
stateDiagram-v2
    [*] --> AguardandoPerguntaState : Inicialização
    AguardandoPerguntaState --> ProcessandoState : processarPergunta(pergunta)
    ProcessandoState --> ProcessandoState : processarPergunta() [ignorado / bloqueado]
    ProcessandoState --> RespondidoState : finalizarResposta(resposta)
    RespondidoState --> AguardandoPerguntaState : processarPergunta(novaPergunta)
```

---

## 3. Padrão Command

Arquivo PlantUML: [`command.puml`](./command.puml)

```mermaid
classDiagram
    class Comando {
        <<interface>>
        +executar() void
    }
    class EnviarPerguntaCommand {
        -chatService: ChatService
        -pergunta: String
        +executar() void
    }
    class CarregarDocumentoCommand {
        -chatService: ChatService
        -arquivo: MultipartFile
        -nomeDocumento: String
        -textoDireto: String
        +executar() void
    }
    class LimparDocumentoCommand {
        -chatService: ChatService
        +executar() void
    }
    class AlternarEstrategiaCommand {
        -chatService: ChatService
        -nomeEstrategia: String
        +executar() void
    }
    class ChatController {
        <<Invoker>>
        -chatService: ChatService
    }
    class ChatService {
        <<Receiver>>
    }

    Comando <|.. EnviarPerguntaCommand
    Comando <|.. CarregarDocumentoCommand
    Comando <|.. LimparDocumentoCommand
    Comando <|.. AlternarEstrategiaCommand

    ChatController ..> Comando : invoca
    EnviarPerguntaCommand --> ChatService : receiver
    CarregarDocumentoCommand --> ChatService : receiver
    LimparDocumentoCommand --> ChatService : receiver
    AlternarEstrategiaCommand --> ChatService : receiver
```

---

## 4. Padrão Strategy

Arquivo PlantUML: [`strategy.puml`](./strategy.puml)

```mermaid
classDiagram
    class EstrategiaResposta {
        <<interface>>
        +gerarResposta(pergunta: String) String
    }
    class SpringAiKnowledgeStrategy {
        <<@Primary>>
        -chatClient: ChatClient
        -conhecimentoService: ConhecimentoService
        +gerarResposta(pergunta: String) String
    }
    class EcoStrategy {
        +gerarResposta(pergunta: String) String
    }
    class ChatService {
        <<Context>>
        -estrategiaAtual: EstrategiaResposta
        +selecionarEstrategia(nome: String) void
        +enviarPergunta(pergunta: String) void
    }

    EstrategiaResposta <|.. SpringAiKnowledgeStrategy
    EstrategiaResposta <|.. EcoStrategy
    ChatService o--> EstrategiaResposta : delega execução
    SpringAiKnowledgeStrategy --> ConhecimentoService : obtém prompt contextualizado
```

---

## 5. Padrão Observer

Arquivo PlantUML: [`observer.puml`](./observer.puml)

```mermaid
classDiagram
    class ObservadorResposta {
        <<interface>>
        +onRespostaPronta(resposta: Mensagem) void
    }
    class LogObservador {
        +onRespostaPronta(resposta: Mensagem) void
    }
    class MetricasObservador {
        -totalRespostas: int
        -totalCaracteres: long
        -ultimaRespostaEm: LocalDateTime
        +onRespostaPronta(resposta: Mensagem) void
        +getTotalRespostas() int
        +getMediaCaracteres() double
    }
    class ChatService {
        <<Subject>>
        -observadores: List~ObservadorResposta~
        +adicionarObservador(obs: ObservadorResposta) void
        -notificarObservadores(resposta: Mensagem) void
    }

    ObservadorResposta <|.. LogObservador
    ObservadorResposta <|.. MetricasObservador
    ChatService o--> ObservadorResposta : notifica quando resposta conclui
```

---

## 6. Padrão Composite (GUI Thymeleaf)

Arquivo PlantUML: [`composite.puml`](./composite.puml)

```mermaid
graph TD
    Root["chat.html (Componente Raiz)"]
    GalhoPainel["painel-conhecimento.html (Galho / Painel de Conhecimento)"]
    GalhoLista["lista-mensagens.html (Galho / Container da Conversa)"]
    FolhaDoc["Informações do Documento / Ações"]
    FolhaMetricas["Telemetria do Observer"]
    FolhaMsg1["mensagem.html (Folha 1 - Usuário)"]
    FolhaMsg2["mensagem.html (Folha 2 - Assistente IA)"]
    FolhaMsgN["mensagem.html (Folha N)"]

    Root --> GalhoPainel
    Root --> GalhoLista
    GalhoPainel --> FolhaDoc
    GalhoPainel --> FolhaMetricas
    GalhoLista --> FolhaMsg1
    GalhoLista --> FolhaMsg2
    GalhoLista --> FolhaMsgN
```
