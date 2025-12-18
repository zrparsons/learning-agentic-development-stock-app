output "namespace" {
  description = "The Kubernetes namespace where resources are deployed"
  value       = kubernetes_namespace.stock_app.metadata[0].name
}

output "backend_service_name" {
  description = "Name of the backend service"
  value       = kubernetes_service.backend.metadata[0].name
}

output "frontend_service_name" {
  description = "Name of the frontend service"
  value       = kubernetes_service.frontend.metadata[0].name
}

output "postgres_service_name" {
  description = "Name of the PostgreSQL service"
  value       = kubernetes_service.postgres.metadata[0].name
}

output "backend_service_type" {
  description = "Type of the backend service"
  value       = kubernetes_service.backend.spec[0].type
}

output "frontend_service_type" {
  description = "Type of the frontend service"
  value       = kubernetes_service.frontend.spec[0].type
}

output "instructions" {
  description = "Instructions for accessing the application"
  value       = <<-EOT
    Application deployed successfully!
    
    To get the service endpoints, run:
      kubectl get svc -n ${var.namespace}
    
    To check pod status:
      kubectl get pods -n ${var.namespace}
    
    To view backend logs:
      kubectl logs -n ${var.namespace} -l app=backend
    
    To view frontend logs:
      kubectl logs -n ${var.namespace} -l app=frontend
    
    Access the application:
      - Frontend: http://<frontend-lb-ip>:3000
      - Backend API: http://<backend-lb-ip>:8080
  EOT
}
