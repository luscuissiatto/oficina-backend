terraform {
  required_providers {
    kind = {
      source  = "tehcyx/kind"
      version = "0.0.13"
    }
  }
}

provider "kind" {}

resource "kind_cluster" "oficina_cluster" {
  name = "oficina-cluster"

  kind_config {
    kind        = "Cluster"
    api_version = "kind.x-k8s.io/v1alpha4"

    node {
      role = "control-plane"
      extra_port_mappings {
        container_port = 30001      # Porta DENTRO do cluster
        host_port      = 8081       # Porta FORA do cluster (na sua máquina)
        protocol       = "TCP"
      }
    }
  }
}