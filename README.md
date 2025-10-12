# Tech Challenge - Fase 2: Evolução para uma Arquitetura Escalável e Automatizada

## Autor
- **Nome:** Lucas Eduardo Peters Scuissiatto
- **RM:** RM365455
- **Curso:** Pós-Graduação em Arquitetura de Software

---

## 📖 Descrição

Esta é a segunda fase do projeto de backend para um Sistema Integrado de Atendimento para uma oficina mecânica. A solução inicial, desenvolvida na Fase 1, foi evoluída para garantir **qualidade, resiliência e escalabilidade**, incorporando práticas modernas de DevOps e infraestrutura.

O objetivo desta fase foi transformar a aplicação monolítica em um sistema robusto, preparado para alta disponibilidade e automação. Para isso, o código foi refatorado para seguir os princípios da **Clean Architecture**, a infraestrutura foi provisionada como código (**IaC**) com **Terraform**, a orquestração de contêineres é gerenciada pelo **Kubernetes**, e uma pipeline de **CI/CD** com **GitHub Actions** automatiza os processos de teste, build e publicação de imagens.

## ✨ Funcionalidades

O sistema mantém as funcionalidades do MVP da Fase 1, agora sustentadas por uma arquitetura mais avançada:

* [x] **Gestão Completa (CRUD):** Clientes, Veículos, Serviços e Peças.
* [x] **Criação e Gestão de Ordens de Serviço (OS):** Ciclo de vida completo, do recebimento à entrega.
* [x] **Consulta de Status:** Endpoint público para clientes.
* [x] **Notificações por Email:** Orçamentos enviados via MailHog.
* [x] **Análise e Monitoramento:** Cálculo de tempo médio de serviços.
* [x] **Segurança:** Autenticação via API com JWT.

## 🏗️ Arquitetura da Solução e Tecnologias

A arquitetura foi redesenhada para suportar escalabilidade e automação.

### Componentes da Arquitetura

* **Clean Architecture:** O código-fonte foi reestruturado nas camadas `domain` (regras de negócio puras), `application` (casos de uso) e `infrastructure` (detalhes de tecnologia como controllers, repositórios JPA, etc.), garantindo baixo acoplamento e alta testabilidade.

* **Infraestrutura como Código (IaC) & Orquestração:**
    * **Terraform:** Utilizado para provisionar o cluster Kubernetes local de forma automatizada. Os scripts estão na pasta `/infra`.
    * **Kubernetes (Kind):** O cluster é orquestrado pelo Kubernetes, rodando localmente via Kind (Kubernetes in Docker).
    * **Manifestos K8s:** A implantação da aplicação e de suas dependências (PostgreSQL, MailHog) é descrita em ficheiros YAML na pasta `/k8s`, contemplando `Deployments`, `Services`, `ConfigMaps`, `Secrets` e `HPA` para auto-scaling.

* **CI/CD (Integração e Entrega Contínua):**
    * **GitHub Actions:** Uma pipeline automatizada é acionada a cada `push` na branch `main`.
    * **Fluxo:** A pipeline executa os testes unitários, constrói a imagem Docker da aplicação e a publica no **GitHub Container Registry (GHCR)**, pronta para ser implantada.

### Tecnologias Utilizadas
* **Linguagem:** Java 17+
* **Framework Principal:** Spring Boot 3.x
* **Segurança:** Spring Security com JWT
* **Banco de Dados:** PostgreSQL (em contêiner)
* **Infraestrutura como Código:** Terraform
* **Orquestração:** Kubernetes (Kind)
* **Conteinerização:** Docker
* **CI/CD:** GitHub Actions
* **Testes:** JUnit 5, Mockito
* **Documentação da API:** SpringDoc (Swagger)
* **Servidor de Email (Dev):** MailHog

---

## 🚀 Como Executar o Projeto

Existem duas formas de executar o projeto localmente. A primeira, com Terraform e Kubernetes, é o ambiente completo da Fase 2. A segunda, com Docker Compose, é uma alternativa mais simples para desenvolvimento rápido.

### Método 1: Ambiente Completo com Terraform e Kubernetes (Recomendado)

Esta abordagem simula o ambiente de produção, orquestrando todos os serviços com Kubernetes.

**Pré-requisitos:**
* Docker
* Terraform
* Kind
* kubectl

**Passos:**

1.  **Clone o repositório:**
    ```sh
    git clone <URL_DO_SEU_REPOSITORIO>
    cd <NOME_DA_PASTA_DO_PROJETO>
    ```

2.  **Provisione o Cluster Kubernetes:**
    Navegue até a pasta `infra` e execute os comandos do Terraform.
    ```sh
    cd infra
    terraform init
    terraform apply
    ```
    Confirme a execução digitando `yes`. Aguarde a criação do cluster.

3.  **Construa a Imagem Docker da Aplicação:**
    Volte para a raiz do projeto e execute o build.
    ```sh
    cd ..
    docker build -t oficina-app:latest .
    ```

4.  **Carregue a Imagem para o Cluster:**
    O Kind precisa que a imagem local seja carregada para dentro do seu ambiente.
    ```sh
    kind load docker-image oficina-app:latest --name oficina-cluster
    ```

5.  **Implante a Aplicação e os Serviços:**
    Aplique todos os manifestos Kubernetes de uma só vez.
    ```sh
    kubectl apply -f k8s/
    ```

6.  **Verifique se tudo está funcionando:**
    Aguarde um ou dois minutos e verifique o status dos pods. Todos devem estar `Running`.
    ```sh
    kubectl get pods
    ```

### Método 2: Ambiente Simplificado com Docker Compose

Esta abordagem utiliza o `docker-compose.yml` para subir a aplicação e suas dependências diretas, sem a camada de orquestração do Kubernetes.

**Pré-requisitos:**
* Docker

**Passos:**
1.  **Clone o repositório.**
2.  Na raiz do projeto, execute:
    ```sh
    docker compose up --build
    ```

---

## 🔗 Acessando os Serviços (Ambiente Kubernetes)

| Serviço                 | URL de Acesso                                | Como Aceder                                                                                                                                   |
| :---------------------- | :------------------------------------------- |:----------------------------------------------------------------------------------------------------------------------------------------------|
| 🚀 **API (Swagger UI)** | `http://localhost:8081/swagger-ui/index.html` | Aceda diretamente no navegador. A porta `8081` foi mapeada pelo Terraform.                                                                    |
| ✉️ **MailHog (Emails)** | `http://localhost:8025`                      | **Abra um novo terminal** e execute `kubectl port-forward svc/mailhog-service 8025:8025`. Mantenha o terminal aberto e acesse pelo navegador. |

### 🔑 Utilizando a API (Autenticação)

1.  No Swagger, execute o `POST /login` com o corpo:
    ```json
    {
      "username": "admin",
      "password": "password"
    }
    ```
2.  Copie o `token` JWT retornado.
3.  Clique no botão **"Authorize"**, cole o token no campo (precedido por `Bearer `) e confirme.
4.  Agora você pode executar todos os outros endpoints protegidos.

## 🔄 Fluxo de CI/CD

A pipeline no GitHub Actions é acionada a cada `push` na branch `main`. Ela executa os seguintes passos:
1.  **Testa:** Roda todos os testes unitários e de integração com Maven.
2.  **Constrói:** Gera a imagem Docker da aplicação.
3.  **Publica:** Envia a imagem para o GitHub Container Registry, pronta para ser usada em um deploy.

Você pode acompanhar as execuções na aba **[Actions](https://github.com/luscuissiatto/tech-challenge-oficina/actions)** do repositório.