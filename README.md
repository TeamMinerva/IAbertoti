# IAbertoti

<p align="center">
  |
    <a href="#descrição-do-projeto"> Descrição do projeto </a> |
    <a href="#padrões-de-projeto-exigidos"> Padrões de projeto exigidos </a> |
    <a href="#tecnologias-utilizadas"> Tecnologias utilizadas </a> |
    <a href="#primeira-entrega"> Primeira entrega </a> |
    <a href="#equipe"> Equipe </a> |
</p>

## Descrição do projeto
esse projeto faz parte da disciplina de Engenharia de Software III, e tambem como base teorica para a API do 4° Semestre de ADS.
a proposta é criar um agente de IA que siga um padrão simples de respostas, utilizando de Spring Boot em Java, o que é diferente da stack da API que é totalmente web envolvendo typescript + vite e react. mas a ideia e a forma de implementar é a mesma. É uma IA de perguntas e respostas, a gente vai entregar para ela um arquivo de texto, e ela irá extrair fazendo scrapping e depois formulando uma resposta em cima disso.

## Padrões de projeto exigidos
o enunciado pede que a aplicacao use os seguintes padroes, aplicados de forma que de para explicar via UML onde cada um esta na arquitetura:

- State: representa os estados possiveis de uma sessao de conversa (aguardando pergunta, processando, respondido), e cada estado se comporta de um jeito diferente quando recebe uma nova pergunta.
- Command: encapsula as acoes disparadas pela interface (como enviar uma pergunta) em objetos separados, desacoplando a interface da logica que ela executa.
- Strategy: permite trocar a forma como a resposta e gerada sem alterar o resto do sistema, o que futuramente vai permitir plugar um modelo de IA real no lugar de uma resposta simplificada.
- Observer: avisa quem estiver interessado (a propria interface) quando uma resposta fica pronta, em vez da interface ficar checando o tempo todo se ja terminou.
- Composite: usado na estrutura da interface, montada a partir de fragmentos que se compoem uns dentro dos outros.

## Tecnologias utilizadas
<p align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white">
  <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white">
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white">
</p>

## Primeira entrega

### o que esta pronto nesse MVP
- projeto Spring Boot rodando, gerado via Spring Initializr, com Java 21 e as dependencias Spring Web e Thymeleaf.
- modelo basico das mensagens da conversa: `Mensagem` (guarda quem enviou e o conteudo) e `Autor` (enum com os valores usuario e assistente).
- padrao State implementado por completo: a classe `SessaoChat` guarda o estado atual da conversa e delega o processamento da pergunta para ele. os tres estados possiveis sao `AguardandoPerguntaState`, `ProcessandoState` e `RespondidoState`, cada um reagindo de um jeito diferente quando uma pergunta chega.
- padrao Command, encapsulando a acao de enviar uma pergunta pela interface.
- padrao Strategy, com uma estrategia simples de resposta (sem IA de verdade ainda, so pra provar a arquitetura).
- padrao Observer, ligando a geracao da resposta a interface.
- controller Spring MVC e templates Thymeleaf, aplicando o padrao Composite na composicao dos fragmentos da pagina.
- diagramas UML (PlantUML) mostrando onde cada um dos 5 padroes esta na arquitetura, na pasta `docs/uml`.
- documento curto explicando as decisoes tomadas nessa etapa, na pasta `docs`.

### o que fica de fora dessa entrega, de proposito
a parte de IA de verdade (leitura do arquivo de texto, scrapping, geracao de resposta usando um modelo real) ainda nao esta implementada. essa decisao foi tomada porque essa e so a primeira entrega, nao o projeto inteiro, e o foco aqui e deixar a arquitetura com os 5 padroes de projeto ja aplicados e defensavel, sem depender de infraestrutura externa (como um modelo de IA rodando localmente) no dia da entrega. a base ja fica pronta pra receber essa parte depois, sem precisar reescrever o resto.

## Equipe
<div align="center">
    <table>
        <tr>
            <th>Membro</th>
        </tr>
        <tr>
            <td>Guilherme Rosa</td>
        </tr>
        <tr>
            <td>Isaura Batista</td>
        </tr>
        <tr>
            <td>Leticia Furtado</td>
        </tr>
        <tr>
            <td>Fernanda Pereira</td>
        </tr>
        <tr>
            <td>Wendy Mininel</td>
        </tr>
        <tr>
            <td>Gustavo Garcia</td>
        </tr>
        <tr>
            <td>Caio César</td>
        </tr>
    </table>
</div>

