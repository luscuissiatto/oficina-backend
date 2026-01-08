# 🔧 Oficina Mecânica - Backend API

## 📋 Sobre o Projeto
Este repositório contém a aplicação principal (**Core**) do sistema de gestão de oficinas mecânicas. Trata-se de uma API RESTful robusta desenvolvida em **Java com Spring Boot**, responsável por orquestrar todas as regras de negócio, incluindo cadastro de clientes, gestão de veículos e o ciclo de vida completo das Ordens de Serviço (orçamento, aprovação, execução e finalização).

A aplicação foi projetada para rodar em containers dentro de um cluster **Kubernetes (EKS)**, utiliza um banco de dados **PostgreSQL** e autenticação via Token JWT.

## 🔗 Links de Deploy (Ambiente Ativo)
A aplicação está rodando na AWS. Utilize os links abaixo para testar:

* **📘 Documentação Interativa (Swagger UI):**
    [http://acaceab1bacfe47749e001f9d5247641-1379161854.us-east-1.elb.amazonaws.com:8080/swagger-ui/index.html](http://acaceab1bacfe47749e001f9d5247641-1379161854.us-east-1.elb.amazonaws.com:8080/swagger-ui/index.html)
* **🔑 Gerador de Token (Lambda Auth):**
    [https://4ykcf5qj1h.execute-api.us-east-1.amazonaws.com/default/oficina-auth-function](https://4ykcf5qj1h.execute-api.us-east-1.amazonaws.com/default/oficina-auth-function)

---

## 🏗️ Arquitetura e Diagramas

### Visão Geral (Componentes)
A arquitetura segue o padrão de microsserviços/serverless híbrido, utilizando EKS para o Core e Lambda para Auth.

https://www.mermaidchart.com/d/439442c2-4c65-426f-b4d4-027113368b52

### Modelo de Dados (ER)
Estrutura relacional do banco de dados PostgreSQL.

https://www.mermaidchart.com/d/178e8872-a3b7-4227-9fca-086b241b8c32

---

## 🧪 Guia de Uso: Como Testar o Fluxo
Como a API é protegida, você deve seguir a ordem exata abaixo para conseguir utilizar os endpoints.

### Passo 1: Criar um Cliente (Rota Pública)
Acesse o Swagger (Link acima) e utilize o endpoint `POST /clients`.
* **Importante:** Guarde o número do **CPF** cadastrado.
```json
{
  "name": "Seu Nome",
  "document": "12345678900",
  "email": "teste@email.com"
}
```

### Passo 2: Gerar o Token de Acesso
Faça uma requisição `POST` para a URL da Lambda de Autenticação com o CPF cadastrado.

* **URL:** `https://4ykcf5qj1h.execute-api.us-east-1.amazonaws.com/default/oficina-auth-function`
* **Body (JSON):**
```json
{
    "cpf": "12345678900"
}
```
- Resposta: Copie o valor do campo `token`.

### Passo 3: Autenticar no Swagger
1. Volte ao Swagger UI.
2. Clique no botão Authorize (cadeado no topo à direita).
3. Digite: `Bearer SEU_TOKEN_COPIADO`.
4. Clique em Authorize.

### Passo 4: Utilizar a API
Agora você pode acessar rotas protegidas, como criar ordens de serviço (`POST /service-orders`) ou consultar status. O fluxo de autenticação ocorre conforme o diagrama abaixo:

https://www.mermaidchart.com/d/fc257fb3-f4d3-4a35-b105-e9aa7b42cdd0

## 🚀 Tecnologias Utilizadas
- Java 17 (LTS)
- Spring Boot 3.x (Web, Data JPA, Security, Actuator)
- PostgreSQL (Persistência)
- Docker & Kubernetes (Infraestrutura)
- New Relic (Observabilidade)
- Swagger/OpenAPI (Documentação)

## ⚙️ Como Rodar Localmente

**Pré-requisitos**
- Java JDK 17
- Maven
- Docker (para banco de dados)

### 1. Subir o Banco de Dados
```bash
docker run --name oficina-db -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres
```

### 2. Configurar Aplicação
No arquivo `src/main/resources/application.properties`, ajuste as credenciais se necessário:

```bash
spring.datasource.url=jdbc:postgresql://localhost:5432/oficina
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### 3. Executar
```bash
./mvnw spring-boot:run
```
Acesse localmente em: `http://localhost:8080/swagger-ui.html`

## ☁️ CI/CD e Deploy
O deploy é automatizado via **GitHub Actions**:
1. Testes unitários são executados.
2. Build da imagem Docker e push para o Amazon ECR.
3. Atualização automática dos Pods no cluster EKS.
