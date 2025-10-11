# Bloco de configuração do Terraform
terraform {
  required_providers {
    kind = {
      source  = "tehcyx/kind"
      version = "0.0.13"
    }
  }
}

# Bloco de configuração do provedor Kind
provider "kind" {}

# Define o recurso que queremos criar: um cluster Kind
resource "kind_cluster" "oficina_cluster" {
  # Nome do nosso cluster Kubernetes
  name = "oficina-cluster"

  # Configuração específica do Kind.
  kind_config {
    kind        = "Cluster"
    api_version = "kind.x-k8s.io/v1alpha4" # CORRIGIDO: de apiVersion para api_version

    # Lista de nós (máquinas) no nosso cluster. Teremos um.
    node { # CORRIGIDO: de nodes para node
      role = "control-plane" # Nó principal do Kubernetes

      # Mapeamento de portas:
      # Permite que acessemos os serviços rodando DENTRO do cluster
      # a partir do nosso navegador na máquina local (localhost).
      extra_port_mappings {
        container_port = 30001      # Porta DENTRO do cluster
        host_port      = 8081       # Porta FORA do cluster (na sua máquina)
        protocol       = "TCP"
      }
    }
  }
}