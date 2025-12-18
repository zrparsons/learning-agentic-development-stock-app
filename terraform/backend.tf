# Backend ConfigMap
resource "kubernetes_config_map" "backend" {
  metadata {
    name      = "backend-config"
    namespace = kubernetes_namespace.stock_app.metadata[0].name
    
    labels = {
      app         = "backend"
      environment = var.environment
      managed-by  = "terraform"
    }
  }

  data = {
    DB_HOST = "postgres.${var.namespace}.svc.cluster.local"
    DB_PORT = "5432"
  }
}

# Backend Deployment
resource "kubernetes_deployment" "backend" {
  metadata {
    name      = "backend"
    namespace = kubernetes_namespace.stock_app.metadata[0].name
    
    labels = {
      app         = "backend"
      environment = var.environment
      managed-by  = "terraform"
    }
  }

  spec {
    replicas = var.backend_replicas

    selector {
      match_labels = {
        app = "backend"
      }
    }

    template {
      metadata {
        labels = {
          app         = "backend"
          environment = var.environment
        }
      }

      spec {
        # Wait for PostgreSQL to be ready
        init_container {
          name  = "wait-for-postgres"
          image = "busybox:1.35"
          
          command = [
            "sh",
            "-c",
            "until nc -z postgres.${var.namespace}.svc.cluster.local 5432; do echo waiting for postgres; sleep 2; done"
          ]
        }

        image_pull_secrets {
          name = kubernetes_secret.gcp_credentials.metadata[0].name
        }

        container {
          name  = "backend"
          image = var.backend_image

          port {
            container_port = 8080
            name           = "http"
          }

          env {
            name = "DB_HOST"
            value_from {
              config_map_key_ref {
                name = kubernetes_config_map.backend.metadata[0].name
                key  = "DB_HOST"
              }
            }
          }

          env {
            name = "DB_PORT"
            value_from {
              config_map_key_ref {
                name = kubernetes_config_map.backend.metadata[0].name
                key  = "DB_PORT"
              }
            }
          }

          env {
            name = "DB_NAME"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.db_credentials.metadata[0].name
                key  = "DB_NAME"
              }
            }
          }

          env {
            name = "DB_USER"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.db_credentials.metadata[0].name
                key  = "DB_USER"
              }
            }
          }

          env {
            name = "DB_PASSWORD"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.db_credentials.metadata[0].name
                key  = "DB_PASSWORD"
              }
            }
          }

          env {
            name = "JWT_SECRET"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.jwt_secret.metadata[0].name
                key  = "JWT_SECRET"
              }
            }
          }

          resources {
            requests = {
              memory = "512Mi"
              cpu    = "500m"
            }
            limits = {
              memory = "1Gi"
              cpu    = "1000m"
            }
          }
        }
      }
    }
  }

  depends_on = [
    kubernetes_deployment.postgres,
    kubernetes_service.postgres
  ]
}

# Backend Service
resource "kubernetes_service" "backend" {
  metadata {
    name      = "backend"
    namespace = kubernetes_namespace.stock_app.metadata[0].name
    
    labels = {
      app         = "backend"
      environment = var.environment
      managed-by  = "terraform"
    }
  }

  spec {
    selector = {
      app = "backend"
    }

    port {
      name        = "http"
      port        = 8080
      target_port = 8080
      protocol    = "TCP"
    }

    type = "LoadBalancer"
    
  }
}
