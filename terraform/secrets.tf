resource "kubernetes_secret" "db_credentials" {
  metadata {
    name      = "db-credentials"
    namespace = kubernetes_namespace.stock_app.metadata[0].name
    
    labels = {
      app         = "stock-app"
      environment = var.environment
      managed-by  = "terraform"
    }
  }

  data = {
    DB_NAME     = var.db_name
    DB_USER     = var.db_user
    DB_PASSWORD = var.db_password
  }

  type = "Opaque"
}

resource "kubernetes_secret" "jwt_secret" {
  metadata {
    name      = "jwt-secret"
    namespace = kubernetes_namespace.stock_app.metadata[0].name
    
    labels = {
      app         = "stock-app"
      environment = var.environment
      managed-by  = "terraform"
    }
  }

  data = {
    JWT_SECRET = var.jwt_secret
  }

  type = "Opaque"
}

resource "kubernetes_secret" "gcp_credentials" {
  metadata {
    name      = "gcp-credentials"
    namespace = kubernetes_namespace.stock_app.metadata[0].name
    
    labels = {
      app         = "stock-app"
      environment = var.environment
      managed-by  = "terraform"
    }
  }

  data = {
    ".dockerconfigjson" = templatefile("${path.module}/docker-registry-config.json", {
      docker-server   = var.gcp_docker_registry
      docker-username = "_json_key"
      docker-email    = var.gcp_docker_email
      docker-password = replace(replace(file(var.gcp_docker_json_key), "\n", ""), "\"", "\\\"")
      auth            = base64encode("_json_key:${file(var.gcp_docker_json_key)}")
    })
  }


  type = "Opaque"
}
