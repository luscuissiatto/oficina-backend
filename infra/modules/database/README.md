# Módulo Terraform - Banco de Dados PostgreSQL

Este módulo provisiona um banco de dados PostgreSQL gerenciado no Kubernetes com state próprio, variáveis configuráveis e outputs que alimentam o deploy da API.

## Estrutura

- `main.tf`: Recursos principais (ConfigMap, Secret, Deployment, Service)
- `variables.tf`: Variáveis de entrada do módulo
- `outputs.tf`: Outputs que podem ser consumidos por outros módulos ou pela aplicação
- `versions.tf`: Versões requeridas do Terraform

## Uso

```hcl
module "database" {
  source = "./modules/database"

  kubeconfig_path  = var.kubeconfig_path
  namespace        = "default"
  database_name    = "oficina_db"
  database_user    = "admin"
  database_password = var.database_password
  postgres_image   = "postgres:15-alpine"
  replicas         = 1
}
```

## Outputs

O módulo expõe os seguintes outputs:

- `database_host`: Nome do serviço Kubernetes do PostgreSQL
- `database_port`: Porta do banco de dados
- `database_name`: Nome do banco de dados
- `database_user`: Usuário do banco de dados
- `database_url`: URL JDBC completa para conexão
- `configmap_name`: Nome do ConfigMap criado
- `secret_name`: Nome do Secret criado
- `service_name`: Nome do Service criado

## Variáveis Principais

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `database_name` | Nome do banco de dados | `oficina_db` |
| `database_user` | Usuário do banco | `admin` |
| `database_password` | Senha do banco (sensitive) | - |
| `postgres_image` | Imagem Docker do PostgreSQL | `postgres:15-alpine` |
| `replicas` | Número de réplicas | `1` |

## Integração com a API

Os outputs deste módulo podem ser usados para configurar a aplicação:

```hcl
# No deployment da API
env {
  name  = "SPRING_DATASOURCE_URL"
  value = module.database.database_url
}

env {
  name  = "SPRING_DATASOURCE_USERNAME"
  value = module.database.database_user
}
```




