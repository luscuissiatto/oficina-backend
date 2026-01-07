terraform {
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.23"
    }
  }
}

provider "kubernetes" {
  host = kind_cluster.oficina_cluster.endpoint

  client_certificate     = kind_cluster.oficina_cluster.client_certificate
  client_key             = kind_cluster.oficina_cluster.client_key
  cluster_ca_certificate = kind_cluster.oficina_cluster.cluster_ca_certificate
}

module "database" {
  source = "./modules/database"

  namespace        = var.database_namespace
  database_name    = var.database_name
  database_user    = var.database_user
  database_password = var.database_password
  postgres_image   = var.postgres_image
  replicas         = var.database_replicas
}

