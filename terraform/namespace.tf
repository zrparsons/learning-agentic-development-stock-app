resource "kubernetes_namespace" "stock_app" {
  metadata {
    name = var.namespace
    
    labels = {
      name        = var.namespace
      environment = var.environment
      managed-by  = "terraform"
    }
  }
}
