# IAbertoti - Knowledge Assistant com Spring AI

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.2-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/Spring%20AI-1.0.0--M6-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
  <img src="https://img.shields.io/badge/Ollama-Open%20Source%20LLM-black?style=for-the-badge&logo=ollama&logoColor=white">
  <img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white">
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white">
  <img src="https://img.shields.io/badge/JUnit-5-25A162?style=for-the-badge&logo=junit5&logoColor=white">
</p>

<p align="center">
    <a href="#-descrição-do-projeto"> Descrição do Projeto </a> |
    <a href="#-padrões-de-projeto-implementados"> Padrões de Projeto </a> |
    <a href="#-arquitetura-e-diagramas-uml"> Diagramas UML </a> |
    <a href="#-como-executar-o-projeto"> Como Executar </a> |
    <a href="#-roteiro-de-teste-e-demonstração"> Demonstração </a> |
    <a href="#-equipe"> Equipe </a>
</p>

---

## 📌 Descrição do Projeto

Este projeto foi desenvolvido para a disciplina de **Engenharia de Software III** (4º Semestre de ADS - FATEC Prof. Jessen Vidal), sob orientação do **Prof. Giuliano Bertoti**.

A proposta é um **Knowledge Assistant** (Assistente de Conhecimento), categoria de agentes corporativos definida na literatura de IA moderna. O assistente recebe documentos textuais (arquivos `.txt` ou `.md`) fornecidos pelo usuário, ingere seu conteúdo na base de conhecimento em memória e utiliza um **modelo de linguagem aberto (LLM open-source via Ollama)** com **Spring AI** para responder a dúvidas estritamente fundamentadas nas evidências do documento fornecido (RAG - *Retrieval-Augmented Generation* simples).

---

## 🏛️ Padrões de Projeto Implementados

Conforme exigido nas especificações do trabalho, a aplicação adota de forma defensável **5 Design Patterns do GoF**:

| Padrão | Classificação | Onde está na Arquitetura | Papel no Projeto |
| :--- | :--- | :--- | :--- |
| **State** | Comportamental | `br.com.minerva.iabertoti.state` e `model.SessaoChat` | Modela o ciclo de vida da sessão de conversa (`AguardandoPerguntaState` $\rightarrow$ `ProcessandoState` $\rightarrow$ `RespondidoState`), bloqueando interações concorrentes enquanto a IA processa a resposta. |
| **Command** | Comportamental | `br.com.minerva.iabertoti.command` | Desacopla o `ChatController` das ações do sistema, encapsulando requisições em objetos executáveis (`EnviarPerguntaCommand`, `CarregarDocumentoCommand`, `LimparDocumentoCommand`, `AlternarEstrategiaCommand`). |
| **Strategy** | Comportamental | `br.com.minerva.iabertoti.strategy` | Permite trocar o algoritmo de resposta em tempo de execução: `SpringAiKnowledgeStrategy` (inferência real com Ollama e contexto RAG) e `EcoStrategy` (mock para testes offline). |
| **Observer** | Comportamental | `br.com.minerva.iabertoti.observer` | Notifica observadores desacoplados quando uma resposta fica pronta: `LogObservador` (logs no terminal) e `MetricasObservador` (telemetria de respostas e caracteres processados). |
| **Composite** | Estrutural | `src/main/resources/templates/` (Thymeleaf) | Estrutura a interface web em uma árvore hierárquica de componentes reutilizáveis: Raiz (`chat.html`), Galhos compostos (`painel-conhecimento.html`, `lista-mensagens.html`) e Folhas individuais (`mensagem.html`). |

---

### Detalhamento dos Padrões e Trechos de Código

#### 1. Padrão State
* **Interface:** `EstadoSessao` com os métodos `processarPergunta(SessaoChat sessao, String pergunta)` e `finalizarResposta(SessaoChat sessao, Mensagem resposta)`.
* **Classes Concretas:**
  * `AguardandoPerguntaState`: Aceita a pergunta, insere na sessão e transiciona para `ProcessandoState`.
  * `ProcessandoState`: Bloqueia o envio de novas perguntas durante a inferência da LLM e, ao receber `finalizarResposta`, adiciona a resposta e transiciona para `RespondidoState`.
  * `RespondidoState`: Ao receber uma nova pergunta, volta para `AguardandoPerguntaState` e inicia novo ciclo.

```java
// Exemplo em ProcessandoState.java:
public class ProcessandoState implements EstadoSessao {
    @Override
    public void processarPergunta(SessaoChat sessao, String pergunta) {
        System.out.println("[State] Aguarde, ainda estou processando a pergunta anterior.");
    }

    @Override
    public void finalizarResposta(SessaoChat sessao, Mensagem resposta) {
        sessao.adicionarMensagem(resposta);
        sessao.mudarEstado(new RespondidoState());
    }
}
```

#### 2. Padrão Command
* **Interface:** `Comando` com o método `executar()`.
* O `ChatController` recebe requisições HTTP e não executa regras de negócio diretamente; ele instancia um comando concreto e chama `comando.executar()`.

```java
// Exemplo em ChatController.java:
@PostMapping("/chat/documento/upload")
public String carregarDocumento(@RequestParam("arquivo") MultipartFile arquivo) {
    Comando comando = new CarregarDocumentoCommand(chatService, arquivo);
    comando.executar();
    return "redirect:/chat";
}
```

#### 3. Padrão Strategy
* **Interface:** `EstrategiaResposta` com o método `gerarResposta(String pergunta)`.
* `SpringAiKnowledgeStrategy` (`@Primary`): Injeta `ChatClient` do Spring AI e `ConhecimentoService`, monta o prompt enriquecido com o documento e invoca o Ollama local.
* `EcoStrategy`: Mock simulado para testes rápidos e desenvolvimento sem necessidade de GPU/LLM rodando.

```java
// Exemplo em SpringAiKnowledgeStrategy.java:
@Component("springAiKnowledgeStrategy")
@Primary
public class SpringAiKnowledgeStrategy implements EstrategiaResposta {
    private final ChatClient chatClient;
    private final ConhecimentoService conhecimentoService;

    @Override
    public String gerarResposta(String pergunta) {
        Prompt prompt = conhecimentoService.montarPrompt(pergunta);
        return chatClient.prompt(prompt).call().content();
    }
}
```

#### 4. Padrão Observer
* **Interface:** `ObservadorResposta` com o método `onRespostaPronta(Mensagem resposta)`.
* `ChatService` atua como *Subject* mantendo uma lista de observadores (`LogObservador`, `MetricasObservador`).
* Ao finalizar a geração da resposta, `notificarObservadores(resposta)` é disparado de forma síncrona/desacoplada, atualizando a telemetria sem engessar o serviço de chat.

#### 5. Padrão Composite na GUI
A interface web foi estruturada em componentes aninhados no Thymeleaf:
* **Raiz (Composite Root):** `chat.html`
* **Galhos (Composite Branches):**
  * `painel-conhecimento.html`: compõe upload de arquivo, estado atual da sessão, troca de estratégia e telemetria do Observer.
  * `lista-mensagens.html`: itera sobre a coleção de mensagens.
* **Folha (Leaf):** `mensagem.html`: renderiza uma unidade individual de mensagem (usuário ou assistente).

---

## 📊 Arquitetura e Diagramas UML

A pasta [`docs/uml/`](./docs/uml/) contém todos os diagramas em formato **PlantUML (`.puml`)** e suas respectivas representações em **Mermaid**:

* 📐 [Visão Geral da Arquitetura (`arquitetura-geral.puml`)](./docs/uml/arquitetura-geral.puml)
* 📐 [Diagrama de Classes e Estados - State (`state.puml`)](./docs/uml/state.puml)
* 📐 [Diagrama de Classes - Command (`command.puml`)](./docs/uml/command.puml)
* 📐 [Diagrama de Classes - Strategy (`strategy.puml`)](./docs/uml/strategy.puml)
* 📐 [Diagrama de Classes - Observer (`observer.puml`)](./docs/uml/observer.puml)
* 📐 [Diagrama de Estrutura - Composite (`composite.puml`)](./docs/uml/composite.puml)
* 📖 [Guia Visual de Diagramas (`docs/uml/README.md`)](./docs/uml/README.md)

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
* **Java 21** instalado e configurado no `PATH`.
* **Ollama** instalado na máquina ([Download Ollama](https://ollama.com/)).

### 1. Iniciar o Modelo Aberto no Ollama
Abra um terminal e execute o modelo open-source configurado (ex: LLaMA 3.1 ou LLaMA 3.2):
```bash
ollama run llama3.1
```
*(O Ollama iniciará o serviço localmente na porta padrão `http://localhost:11434`)*.

> **Dica:** Caso queira executar a aplicação sem iniciar o Ollama, o sistema possui tratamento de erro amigável e permite selecionar a **EcoStrategy** (mock) diretamente na interface gráfica!

### 2. Compilar e Rodar os Testes Unitários
No diretório raiz do projeto:
```bash
# Windows
.\mvnw.cmd clean test

# Linux / macOS
./mvnw clean test
```
*Total de 22 testes unitários automatizados cobrindo todos os comandos, serviços, estados e estratégias.*

### 3. Iniciar a Aplicação Spring Boot
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

### 4. Acessar a Aplicação
Abra seu navegador em:
👉 **[http://localhost:8080/chat](http://localhost:8080/chat)**

---

## 🧪 Roteiro de Teste e Demonstração

1. **Estado Inicial:** Acesse `http://localhost:8080/chat`. O painel indicará `Estado: Aguardando Pergunta`, `Estratégia: SpringAiKnowledgeStrategy` e aviso de que nenhum documento está carregado.
2. **Carregar Base de Conhecimento:**
   * Crie um arquivo de texto de teste (ex.: `regras_empresa.txt`) com o seguinte conteúdo:
     ```
     O horário de funcionamento do suporte técnico é de segunda a sexta, das 08:00 às 17:00.
     O limite de reembolso para despesas com alimentação em viagens é de R$ 95,00 por dia.
     O colaborador tem direito a 3 dias de folga no mês de seu aniversário.
     ```
   * No formulário **Base de Conhecimento**, selecione o arquivo e clique em **Carregar Documento**.
   * Uma mensagem de confirmação aparecerá na conversa e o status do documento ativo será exibido no topo.
3. **Fazer Perguntas à IA (Knowledge Assistant):**
   * Pergunta: *"Qual é o horário do suporte técnico?"*
     * Resposta esperada da LLM: *"O horário de funcionamento do suporte técnico é de segunda a sexta, das 08:00 às 17:00."*
   * Pergunta: *"Qual o valor do reembolso para combustível?"*
     * Resposta esperada da LLM: *"O documento fornecido não contém informações sobre reembolso de combustível, apenas para despesas com alimentação (R$ 95,00 por dia)."*
4. **Testar Alternância de Strategy (Mock vs IA):**
   * No seletor de estratégia, mude para `EcoStrategy` e clique em **Trocar**.
   * Envie uma pergunta: o sistema responderá instantaneamente pelo mock demonstrando o padrão Strategy.
5. **Acompanhar Telemetria (Observer):**
   * Observe o rodapé do painel: o contador de respostas e a média de caracteres são atualizados automaticamente a cada interação via `MetricasObservador`.

---

## 👥 Equipe

<div align="center">
    <table>
        <tr>
            <th>Membro</th>
            <th>GitHub</th>
            <th>LinkedIn</th>
            <th>Repositório bertoti</th>
        </tr>
        <tr>
            <td>Caio César</td>
            <td><a href="https://github.com/JkDeltaz"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
            <td><a href="https://www.linkedin.com/in/caio-c%C3%A9sar-santos-79976636a/"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
            <td><a href="https://github.com/JkDeltaz/bertoti"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
        </tr>
        <tr>
            <td>Fernanda Pereira</td>
            <td><a href="https://github.com/Fernanda192028"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
            <td><a href="https://www.linkedin.com/in/fernanda-pereira-252076362"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
            <td><a href="https://github.com/Fernanda192028/bertoti"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
        </tr>
        <tr>
            <td>Guilherme Rosa</td>
            <td><a href="https://github.com/GuilhermeMRosa"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
            <td><a href="https://www.linkedin.com/in/guilherme-rosa-518047387/"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
            <td><a href="https://github.com/GuilhermeMRosa/bertoti"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
        </tr>
        <tr>
            <td>Gustavo Garcia</td>
            <td><a href="https://github.com/gustav0-gg"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
            <td><a href="https://www.linkedin.com/in/ggg-"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
            <td><a href="https://github.com/gustav0-gg/bertoti"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
        </tr>
        <tr>
            <td>Isaura Batista</td>
            <td><a href="https://github.com/sunrhse"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
            <td><a href="https://www.linkedin.com/in/isaura-de-lourdes-0561742aa"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
            <td><a href="https://github.com/sunrhse/bertoti"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
        </tr>
        <tr>
            <td>Leticia Furtado</td>
            <td><a href="https://github.com/LeticiaG24"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
            <td><a href="https://www.linkedin.com/in/letícia-furtado-053b6033a"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
            <td><a href="https://github.com/LeticiaG24/bertoti"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
        </tr>
        <tr>
            <td>Wendy Mininel</td>
            <td><a href="https://github.com/WendyMininel"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
            <td><a href="https://www.linkedin.com/in/wendy-mininel-279300353"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
            <td><a href="https://github.com/WendyMininel/bertoti"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
        </tr>
    </table>
</div>
