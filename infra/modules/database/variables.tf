variable "namespace" {
  description = "Namespace do Kubernetes onde o banco será provisionado"
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
}

variable "postgres_image" {
  description = "Imagem Docker do PostgreSQL"
  type        = string
  default     = "postgres:15-alpine"
}

variable "replicas" {
  description = "Número de réplicas do PostgreSQL"
  type        = number
  default     = 1
}

variable "configmap_name" {
  description = "Nome do ConfigMap do PostgreSQL"
  type        = string
  default     = "postgres-configmap"
}

variable "secret_name" {
  description = "Nome do Secret do PostgreSQL"
  type        = string
  default     = "postgres-secret"
}

variable "deployment_name" {
  description = "Nome do Deployment do PostgreSQL"
  type        = string
  default     = "postgres-deployment"
}

variable "service_name" {
  description = "Nome do Service do PostgreSQL"
  type        = string
  default     = "postgres-service"
}

variable "resources_requests_cpu" {
  description = "CPU solicitada para o PostgreSQL"
  type        = string
  default     = "250m"
}

variable "resources_requests_memory" {
  description = "Memória solicitada para o PostgreSQL"
  type        = string
  default     = "512Mi"
}

variable "resources_limits_cpu" {
  description = "Limite de CPU para o PostgreSQL"
  type        = string
  default     = "500m"
}

variable "resources_limits_memory" {
  description = "Limite de memória para o PostgreSQL"
  type        = string
  default     = "1Gi"
}

