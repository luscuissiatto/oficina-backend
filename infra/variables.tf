variable "database_namespace" {
  description = "Namespace do Kubernetes para o banco de dados"
  type        = string
  default     = "default"
}

variable "database_name" {
  description = "Nome do banco de dados PostgreSQL"
  type        = string
  default     = "oficina_db"
}

variable "database_user" {
  description = "Usuário do banco de dados PostgreSQL"
  type        = string
  default     = "admin"
}

variable "database_password" {
  description = "Senha do banco de dados PostgreSQL"
  type        = string
  sensitive   = true
  default     = "senhaSuperSecreta"
}

variable "postgres_image" {
  description = "Imagem Docker do PostgreSQL"
  type        = string
  default     = "postgres:15-alpine"
}

variable "database_replicas" {
  description = "Número de réplicas do PostgreSQL"
  type        = number
  default     = 1
}

