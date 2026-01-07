# Provider deve ser configurado no módulo pai

# ConfigMap para configurações do PostgreSQL
resource "kubernetes_config_map" "postgres_config" {
  metadata {
    name      = var.configmap_name
    namespace = var.namespace
  }

  data = {
    POSTGRES_DB   = var.database_name
    POSTGRES_USER = var.database_user
  }
}

# Secret para credenciais do PostgreSQL
resource "kubernetes_secret" "postgres_secret" {
  metadata {
    name      = var.secret_name
    namespace = var.namespace
  }

  type = "Opaque"

  data = {
    postgres-password = base64encode(var.database_password)
  }
}

# Deployment do PostgreSQL
resource "kubernetes_deployment" "postgres" {
  metadata {
    name      = var.deployment_name
    namespace = var.namespace
    labels = {
      app = "postgres"
    }
  }

  spec {
    replicas = var.replicas

    selector {
      match_labels = {
        app = "postgres"
      }
    }

    template {
      metadata {
        labels = {
          app = "postgres"
        }
      }

      spec {
        container {
          name  = "postgres"
          image = var.postgres_image

          port {
            container_port = 5432
          }

          env_from {
            config_map_ref {
              name = kubernetes_config_map.postgres_config.metadata[0].name
            }
          }

          env_from {
            secret_ref {
              name = kubernetes_secret.postgres_secret.metadata[0].name
            }
          }

          env {
            name = "POSTGRES_PASSWORD"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.postgres_secret.metadata[0].name
                key  = "postgres-password"
              }
            }
          }

          resources {
            requests = {
              cpu    = var.resources_requests_cpu
              memory = var.resources_requests_memory
            }
            limits = {
              cpu    = var.resources_limits_cpu
              memory = var.resources_limits_memory
            }
          }

          volume_mount {
            name       = "postgres-storage"
            mount_path = "/var/lib/postgresql/data"
          }
        }

        volume {
          name = "postgres-storage"
          empty_dir {}
        }
      }
    }
  }
}

# Service do PostgreSQL
resource "kubernetes_service" "postgres" {
  metadata {
    name      = var.service_name
    namespace = var.namespace
  }

  spec {
    selector = {
      app = "postgres"
    }

    port {
      protocol    = "TCP"
      port        = 5432
      target_port = 5432
    }

    type = "ClusterIP"
  }
}

