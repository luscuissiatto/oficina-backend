output "database_host" {
  description = "Host do banco de dados PostgreSQL (nome do serviço Kubernetes)"
  value       = kubernetes_service.postgres.metadata[0].name
}

output "database_port" {
  description = "Porta do banco de dados PostgreSQL"
  value       = kubernetes_service.postgres.spec[0].port[0].port
}

output "database_name" {
  description = "Nome do banco de dados"
  value       = var.database_name
}

output "database_user" {
  description = "Usuário do banco de dados"
  value       = var.database_user
}

output "database_url" {
  description = "URL de conexão JDBC completa para o banco de dados"
  value       = "jdbc:postgresql://${kubernetes_service.postgres.metadata[0].name}:${kubernetes_service.postgres.spec[0].port[0].port}/${var.database_name}"
}

output "configmap_name" {
  description = "Nome do ConfigMap criado"
  value       = kubernetes_config_map.postgres_config.metadata[0].name
}

output "secret_name" {
  description = "Nome do Secret criado"
  value       = kubernetes_secret.postgres_secret.metadata[0].name
}

output "service_name" {
  description = "Nome do Service do PostgreSQL"
  value       = kubernetes_service.postgres.metadata[0].name
}




