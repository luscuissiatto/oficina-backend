output "cluster_name" {
  description = "Nome do cluster Kubernetes criado"
  value       = kind_cluster.oficina_cluster.name
}

output "cluster_endpoint" {
  description = "Endpoint do cluster Kubernetes"
  value       = kind_cluster.oficina_cluster.endpoint
}

# Outputs do módulo de banco de dados
output "database_host" {
  description = "Host do banco de dados PostgreSQL"
  value       = module.database.database_host
}

output "database_port" {
  description = "Porta do banco de dados PostgreSQL"
  value       = module.database.database_port
}

output "database_name" {
  description = "Nome do banco de dados"
  value       = module.database.database_name
}

output "database_user" {
  description = "Usuário do banco de dados"
  value       = module.database.database_user
}

output "database_url" {
  description = "URL de conexão JDBC completa para o banco de dados"
  value       = module.database.database_url
  sensitive   = true
}

output "database_configmap_name" {
  description = "Nome do ConfigMap do banco de dados"
  value       = module.database.configmap_name
}

output "database_secret_name" {
  description = "Nome do Secret do banco de dados"
  value       = module.database.secret_name
}

output "database_service_name" {
  description = "Nome do Service do banco de dados"
  value       = module.database.service_name
}




