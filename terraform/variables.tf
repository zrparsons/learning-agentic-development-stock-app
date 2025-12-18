variable "kubeconfig_path" {
  description = "Path to the kubeconfig file"
  type        = string
  default     = "~/.kube/config"
}

variable "namespace" {
  description = "Kubernetes namespace for the application"
  type        = string
  default     = "stock-app"
}

variable "backend_image" {
  description = "Docker image for the backend application"
  type        = string
}

variable "frontend_image" {
  description = "Docker image for the frontend application"
  type        = string
}

variable "postgres_image" {
  description = "PostgreSQL Docker image"
  type        = string
  default     = "postgres:15"
}


variable "db_name" {
  description = "PostgreSQL database name"
  type        = string
  default     = "stockapp"
}

variable "db_user" {
  description = "PostgreSQL database user"
  type        = string
  default     = "stockapp"
}

variable "db_password" {
  description = "PostgreSQL database password"
  type        = string
  sensitive   = true
}

variable "jwt_secret" {
  description = "JWT secret for authentication"
  type        = string
  sensitive   = true
}

variable "environment" {
  description = "Environment name (dev, staging, prod)"
  type        = string
  default     = "dev"
}

variable "backend_replicas" {
  description = "Number of backend replicas"
  type        = number
  default     = 2
}

variable "frontend_replicas" {
  description = "Number of frontend replicas"
  type        = number
  default     = 2
}

  variable "gcp_docker_registry" {
    description = "The GCP Docker registry URL"
    type        = string
  }

  variable "gcp_docker_email" {
    description = "The email associated with the GCP Docker registry service account"
    type        = string
  }

  variable "gcp_docker_json_key" {
    description = "The JSON key for the GCP Docker registry service account"
    type        = string
    sensitive   = true
  }