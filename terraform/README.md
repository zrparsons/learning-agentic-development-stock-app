# Stock App Kubernetes Terraform Configuration

This directory contains Terraform configuration files for deploying the Stock App to Kubernetes.

## Quick Start

1. **Copy the example variables file:**
   ```bash
   cp terraform.tfvars.example terraform.tfvars
   ```

2. **Edit terraform.tfvars** with your configuration (registry URLs, passwords, etc.)

3. **Initialize Terraform:**
   ```bash
   terraform init
   ```

4. **Review the plan:**
   ```bash
   terraform plan
   ```

5. **Deploy:**
   ```bash
   terraform apply
   ```

6. **Get service endpoints:**
   ```bash
   kubectl get svc -n stock-app
   ```

For detailed instructions, see [DEPLOYMENT.md](../DEPLOYMENT.md) in the project root.

## File Structure

- `providers.tf` - Terraform and Kubernetes provider configuration
- `variables.tf` - Input variable definitions
- `outputs.tf` - Output values after deployment
- `main.tf` - Main configuration entry point
- `namespace.tf` - Kubernetes namespace resource
- `secrets.tf` - Kubernetes secrets (database, JWT)
- `database.tf` - PostgreSQL StatefulSet, PVC, and Service
- `backend.tf` - Backend Deployment, Service, and ConfigMap
- `frontend.tf` - Frontend Deployment and Service
- `terraform.tfvars.example` - Example variable values
- `.gitignore` - Git ignore rules for Terraform files

## Resources Created

### Namespace
- `stock-app` namespace

### Secrets
- `db-credentials` - PostgreSQL credentials
- `jwt-secret` - JWT authentication secret

### Database
- PostgreSQL Deployment (1 replica)
- Ephemeral storage (data lost on pod restart)
- ClusterIP Service

> **⚠️ Warning:** Database uses ephemeral storage - data will be lost on restart. For production, use a managed database service.

### Backend
- Deployment (2 replicas by default)
- LoadBalancer Service (port 8080)
- ConfigMap for application configuration

### Frontend
- Deployment (2 replicas by default)
- LoadBalancer Service (port 3000)

## Configuration

All configuration is managed through variables in `terraform.tfvars`.

Required variables:
- `backend_image` - Backend Docker image URL
- `frontend_image` - Frontend Docker image URL
- `db_password` - PostgreSQL password
- `jwt_secret` - JWT secret key

Optional variables (with defaults):
- `kubeconfig_path` - Path to kubeconfig (default: ~/.kube/config)
- `namespace` - Kubernetes namespace (default: stock-app)
- `postgres_image` - PostgreSQL image (default: postgres:15)
- `backend_replicas` - Backend replica count (default: 2)
- `frontend_replicas` - Frontend replica count (default: 2)

## Outputs

After deployment, Terraform outputs:
- Namespace name
- Service names
- Instructions for accessing the application

View outputs:
```bash
terraform output
```

## Updating

To update the deployment:
1. Modify `terraform.tfvars` or Terraform files
2. Run `terraform plan` to preview changes
3. Run `terraform apply` to apply changes

## Cleanup

To destroy all resources:
```bash
terraform destroy
```

**Warning:** This deletes all data including the database!

## Notes

- The `.tfvars` file is ignored by git (contains sensitive data)
- Backend waits for PostgreSQL to be ready before starting
- LoadBalancer services may take a few minutes to get external IPs
- For local clusters, consider changing services to NodePort type
