# Frontend Deployment
resource "kubernetes_deployment" "frontend" {
  metadata {
    name      = "frontend"
    namespace = kubernetes_namespace.stock_app.metadata[0].name
    
    labels = {
      app         = "frontend"
      environment = var.environment
      managed-by  = "terraform"
    }
  }

  spec {
    replicas = var.frontend_replicas

    selector {
      match_labels = {
        app = "frontend"
      }
    }

    template {
      metadata {
        labels = {
          app         = "frontend"
          environment = var.environment
        }
      }

      spec {
        image_pull_secrets {
          name = kubernetes_secret.gcp_credentials.metadata[0].name
        }

        container {
          name  = "frontend"
          image = var.frontend_image

          port {
            container_port = 3000
            name           = "http"
          }

          resources {
            requests = {
              memory = "256Mi"
              cpu    = "250m"
            }
            limits = {
              memory = "512Mi"
              cpu    = "500m"
            }
          }

          liveness_probe {
            http_get {
              path = "/"
              port = 3000
            }
            initial_delay_seconds = 30
            period_seconds        = 10
            timeout_seconds       = 5
            failure_threshold     = 3
          }

          readiness_probe {
            http_get {
              path = "/"
              port = 3000
            }
            initial_delay_seconds = 10
            period_seconds        = 5
            timeout_seconds       = 3
            failure_threshold     = 3
          }
        }
      }
    }
  }

  depends_on = [
    kubernetes_deployment.backend,
    kubernetes_service.backend
  ]
}

# Frontend Service
resource "kubernetes_service" "frontend" {
  metadata {
    name      = "frontend"
    namespace = kubernetes_namespace.stock_app.metadata[0].name
    
    labels = {
      app         = "frontend"
      environment = var.environment
      managed-by  = "terraform"
    }
  }

  spec {
    selector = {
      app = "frontend"
    }

    port {
      name        = "http"
      port        = 3000
      target_port = 3000
      protocol    = "TCP"
    }

    type = "LoadBalancer"
  }
}
