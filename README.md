# IAbertoti

<p align="center">
  |
    <a href="#descrição-do-projeto"> Descrição do projeto </a> |
    <a href="#padrões-de-projeto-exigidos"> Padrões de projeto exigidos </a> |
    <a href="#tecnologias-utilizadas"> Tecnologias utilizadas </a> |
    <a href="#equipe"> Equipe </a> |
</p>

## Descrição do projeto
projeto da disciplina de API (4° Semestre de ADS), feito em Spring Boot/Java. é uma IA de perguntas e respostas: a gente entrega um arquivo de texto pra ela, e ela extrai o conteúdo e formula uma resposta em cima disso.

## Padrões de projeto exigidos
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

## Equipe
<div align="center">
    <table>
        <tr>
            <th>Membro</th>
            <th>Github</th>
            <th>Linkedin</th>
        </tr>
        <tr>
            <td>Guilherme Rosa</td>
            <td><a href="https://github.com/GuilhermeMRosa"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
            <td><a href="https://www.linkedin.com/in/guilherme-rosa-518047387/"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
        </tr>
        <tr>
            <td>Isaura Batista</td>
            <td><a href="https://github.com/sunrhse"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
            <td><a href="https://www.linkedin.com/in/isaura-de-lourdes-0561742aa"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
        </tr>
        <tr>
            <td>Leticia Furtado</td>
            <td><a href="https://github.com/LeticiaG24"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
            <td><a href="https://www.linkedin.com/in/letícia-furtado-053b6033a"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
        </tr>
        <tr>
            <td>Fernanda Pereira</td>
            <td><a href="https://github.com/Fernanda192028"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
            <td><a href="https://www.linkedin.com/in/fernanda-pereira-252076362"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
        </tr>
        <tr>
            <td>Wendy Mininel</td>
            <td><a href="https://github.com/WendyMininel"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
            <td><a href="https://www.linkedin.com/in/wendy-mininel-279300353"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
        </tr>
        <tr>
            <td>Gustavo Garcia</td>
            <td><a href="https://github.com/gustav0-gg"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
            <td><a href="https://www.linkedin.com/in/ggg-"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
        </tr>
        <tr>
            <td>Caio César</td>
            <td><a href="https://github.com/JkDeltaz"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
            <td><a href="https://www.linkedin.com/in/caio-c%C3%A9sar-santos-79976636a/"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
        </tr>
    </table>
</div>

