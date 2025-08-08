# Tech Challenge - Fase 1: Sistema de Gestão para Oficina Mecânica

## 📖 Descrição

Este projeto é o backend para um Sistema Integrado de Atendimento e Execução de Serviços para uma oficina mecânica. A aplicação foi desenvolvida como parte do Tech Challenge da Pós-Graduação em Arquitetura de Software, com o objetivo de centralizar e organizar os processos da oficina, que antes eram manuais e descentralizados. O sistema foca na gestão de ordens de serviço, clientes, veículos e no controle de peças, aplicando conceitos de Domain-Driven Design (DDD) para garantir um software de alta qualidade e alinhado às regras de negócio.

## ✨ Funcionalidades Implementadas

O MVP (Minimum Viable Product) do backend implementa as seguintes funcionalidades obrigatórias:

* [x] **Gestão de Clientes (CRUD):** Criação, leitura, atualização e exclusão de clientes.
* [x] **Gestão de Veículos (CRUD):** Gerenciamento de veículos, sempre associados a um cliente.
* [x] **Gestão de Serviços (CRUD):** Gerenciamento dos serviços oferecidos pela oficina.
* [x] **Gestão de Peças (CRUD):** Gerenciamento de peças e insumos, com controle de estoque.
* [x] **Criação e Gestão de Ordens de Serviço (OS):**
    * Criação de OS associando cliente, veículo, serviços e peças (com quantidade).
    * Cálculo automático do orçamento total.
    * Atualização de status do ciclo de vida da OS (ex: `RECEIVED`, `IN_PROGRESS`, `COMPLETED`).
    * Lógica de negócio para validar e abater o estoque ao iniciar um serviço.
* [x] **Consulta Cliente:** Endpoint para o cliente consultar o status da sua OS pelo documento e placa do veículo.
* [x] **Notificações por Email:** Envio de email para o cliente com o orçamento quando o diagnóstico é finalizado (simulado via MailHog).
* [x] **Análise e Monitoramento:** Endpoint que calcula o tempo médio de execução para cada tipo de serviço finalizado.
* [x] **Segurança:** Autenticação via API com JWT para todos os endpoints administrativos.
* [x] **Validação:** Validação de dados de entrada para CPF/CNPJ e placa de veículo.

## 🏗️ Arquitetura e Tecnologias Utilizadas

O projeto foi construído como um **backend monolítico** utilizando uma **arquitetura em camadas** fortemente inspirada nos princípios do **Domain-Driven Design (DDD)**.

* **Linguagem:** Java 17+
* **Framework Principal:** Spring Boot 3.x
* **Banco de Dados:** PostgreSQL
* **Persistência:** Spring Data JPA / Hibernate
* **Segurança:** Spring Security com autenticação JWT
* **Documentação da API:** SpringDoc (Swagger)
* **Testes:** JUnit 5, Mockito, MockMvc
* **Qualidade e Cobertura de Código:** SonarQube, JaCoCo
* **Ambiente de Desenvolvimento:** Docker

## 🚀 Como Executar o Projeto

Para executar o ambiente completo localmente, você precisará ter os seguintes pré-requisitos instalados:
* Git
* Java (JDK 17 ou superior)
* Maven
* Docker

**Siga os passos abaixo:**

1.  **Clone o repositório:**
    ```sh
    git clone <URL_DO_SEU_REPOSITORIO_PRIVADO>
    cd <NOME_DA_PASTA_DO_PROJETO>
    ```

2.  **Construa e inicie os contêineres:**
    O `docker-compose` irá orquestrar a construção da imagem da sua aplicação e a inicialização de todos os serviços (Aplicação, Banco de Dados, MailHog e SonarQube). Execute o comando na raiz do projeto:
    ```sh
    docker compose up --build
    ```
    Aguarde alguns minutos, especialmente na primeira vez, para que todas as imagens sejam baixadas e os serviços iniciem. O SonarQube pode demorar um pouco mais para ficar operacional.

## 🔗 Acessando os Serviços

Após a inicialização completa, os seguintes serviços estarão disponíveis:

| Serviço | URL de Acesso Local | Descrição |
| :--- | :--- | :--- |
| 🚀 **API (Swagger UI)** | `http://localhost:8080/swagger-ui/index.html` | Interface para interagir e testar todos os endpoints da API. |
| ✉️ **MailHog (Emails)** | `http://localhost:8025` | Caixa de entrada falsa que captura todos os emails enviados pela aplicação. |
| 📊 **SonarQube (Análise)** | `http://localhost:9000` | Dashboard de análise de qualidade e segurança. (Login: `admin`, Senha: `admin`). |
| 🐘 **Banco de Dados** | `localhost:5432` | Conexão direta com o PostgreSQL. Use um cliente como DBeaver ou pgAdmin. |

**Credenciais do Banco de Dados (para DBeaver):**
* **Host:** `localhost`
* **Porta:** `5432`
* **Banco de Dados:** `oficina_db`
* **Usuário:** `user`
* **Senha:** `password`

### 🔑 Utilizando a API (Autenticação)

Todos os endpoints administrativos são protegidos. Para utilizá-los:
1.  No Swagger, execute o endpoint `POST /login` com o seguinte corpo:
    ```json
    {
      "username": "admin",
      "password": "password"
    }
    ```
2.  Copie o `token` JWT retornado na resposta.
3.  Clique no botão **"Authorize"** no topo da página do Swagger, cole o token no campo "Value" (precedido por `Bearer `) e confirme.
4.  Agora você pode executar todos os outros endpoints.

## 🧪 Executando Testes e Análises

Para executar os testes e análises de qualidade localmente, sem precisar do Docker:

1.  **Executar Testes e Gerar Relatório de Cobertura:**
    ```sh
    mvn clean install
    ```
    O relatório de cobertura de testes do JaCoCo estará disponível em `target/site/jacoco/index.html`.

2.  **Executar Análise do SonarQube:**
    * Garanta que o SonarQube está rodando (`docker compose up sonarqube`).
    * Gere um token de acesso na interface do Sonar.
    * Execute o comando:
    ```sh
    mvn verify sonar:sonar -Dsonar.token=<SEU_TOKEN_DO_SONAR>
    ```