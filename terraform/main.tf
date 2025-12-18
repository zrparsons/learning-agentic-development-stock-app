# Main Terraform configuration for Stock App Kubernetes deployment
#
# This file serves as the entry point for the Terraform configuration.
# All resources are organized into separate files for better maintainability:
#
# - providers.tf: Provider configuration
# - variables.tf: Input variables
# - outputs.tf: Output values
# - namespace.tf: Kubernetes namespace
# - secrets.tf: Kubernetes secrets
# - database.tf: PostgreSQL StatefulSet and Service
# - backend.tf: Backend Deployment, Service, and ConfigMap
# - frontend.tf: Frontend Deployment and Service
#
# To deploy:
#   1. Create terraform.tfvars with your configuration
#   2. Run: terraform init
#   3. Run: terraform plan
#   4. Run: terraform apply
