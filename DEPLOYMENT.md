# Kubernetes Deployment Guide

This guide explains how to deploy the Stock App to a Kubernetes cluster using Terraform.

## Prerequisites

Before deploying, ensure you have the following installed and configured:

- **Docker** - For building container images
- **Kubernetes cluster** - Access to an existing Kubernetes cluster
- **kubectl** - Kubernetes command-line tool configured to access your cluster
- **Terraform** >= 1.0 - Infrastructure as Code tool
- **Container registry** - Docker Hub, AWS ECR, GCR, or similar (for storing images)

## Architecture Overview

The deployment includes:

- **PostgreSQL Deployment** - Database with ephemeral storage (data lost on restart - dev only)
- **Backend Deployment** - Kotlin/Ktor API (2 replicas)
- **Frontend Deployment** - React app with Nginx (2 replicas)
- **Services** - LoadBalancer services for frontend and backend
- **ConfigMaps** - Application configuration
- **Secrets** - Database credentials and JWT secret

> **⚠️ Warning:** The PostgreSQL database uses ephemeral storage. All data will be lost when the pod restarts. This is suitable for development/testing only. For production, use a managed database service or enable persistent volumes.

## Deployment Steps

### Step 1: Build Docker Images

Build the backend and frontend Docker images:

```bash
# Build backend
cd backend
docker build -t stock-app-backend:latest .

# Build frontend
cd ../frontend
docker build -t stock-app-frontend:latest .
```

### Step 2: Push Images to Registry

Tag and push images to your container registry:

```bash
# Example for Docker Hub
docker tag stock-app-backend:latest <your-username>/stock-app-backend:latest
docker tag stock-app-frontend:latest <your-username>/stock-app-frontend:latest

docker push <your-username>/stock-app-backend:latest
docker push <your-username>/stock-app-frontend:latest
```

**For AWS ECR:**
```bash
# Login to ECR
aws ecr get-login-password --region <region> | docker login --username AWS --password-stdin <account-id>.dkr.ecr.<region>.amazonaws.com

# Create repositories (if not exists)
aws ecr create-repository --repository-name stock-app-backend
aws ecr create-repository --repository-name stock-app-frontend

# Tag and push
docker tag stock-app-backend:latest <account-id>.dkr.ecr.<region>.amazonaws.com/stock-app-backend:latest
docker tag stock-app-frontend:latest <account-id>.dkr.ecr.<region>.amazonaws.com/stock-app-frontend:latest

docker push <account-id>.dkr.ecr.<region>.amazonaws.com/stock-app-backend:latest
docker push <account-id>.dkr.ecr.<region>.amazonaws.com/stock-app-frontend:latest
```

### Step 3: Configure Terraform Variables

Navigate to the terraform directory and create your configuration:

```bash
cd terraform
```

Copy the example variables file:

```bash
cp terraform.tfvars.example terraform.tfvars
```

Edit `terraform.tfvars` with your values:

```hcl
kubeconfig_path = "~/.kube/config"
namespace       = "stock-app"

# Use your registry URLs
backend_image   = "<registry>/stock-app-backend:latest"
frontend_image  = "<registry>/stock-app-frontend:latest"

# Database configuration
db_name     = "stockapp"
db_user     = "stockapp"
db_password = "CHANGE-ME-secure-password-123"

# JWT secret (use a strong random string)
jwt_secret = "CHANGE-ME-your-super-secret-jwt-key"

# Environment
environment = "dev"

# Optional: Adjust resources
backend_replicas  = 2
frontend_replicas = 2
db_storage_size   = "10Gi"
```

**Important Security Notes:**
- Use strong, unique passwords for `db_password`
- Use a long random string for `jwt_secret` (at least 32 characters)
- Never commit `terraform.tfvars` to version control (it's in .gitignore)

### Step 4: Initialize Terraform

Initialize Terraform to download required providers:

```bash
terraform init
```

### Step 5: Preview Changes

Review the resources that will be created:

```bash
terraform plan
```

This will show you all the Kubernetes resources that Terraform will create.

### Step 6: Deploy to Kubernetes

Apply the Terraform configuration:

```bash
terraform apply
```

Type `yes` when prompted to confirm the deployment.

The deployment process will:
1. Create the `stock-app` namespace
2. Create secrets for database and JWT
3. Deploy PostgreSQL Deployment with ephemeral storage
4. Deploy backend application (waits for database to be ready)
5. Deploy frontend application
6. Create LoadBalancer services

### Step 7: Verify Deployment

Check that all pods are running:

```bash
kubectl get pods -n stock-app
```

Expected output:
```
NAME                        READY   STATUS    RESTARTS   AGE
backend-xxxxxxxxxx-xxxxx    1/1     Running   0          2m
backend-xxxxxxxxxx-xxxxx    1/1     Running   0          2m
frontend-xxxxxxxxxx-xxxxx   1/1     Running   0          2m
frontend-xxxxxxxxxx-xxxxx   1/1     Running   0          2m
postgres-0                  1/1     Running   0          3m
```

Check services and get LoadBalancer IPs:

```bash
kubectl get svc -n stock-app
```

Expected output:
```
NAME       TYPE           CLUSTER-IP       EXTERNAL-IP      PORT(S)          AGE
backend    LoadBalancer   10.100.xxx.xxx   <external-ip>    8080:xxxxx/TCP   3m
frontend   LoadBalancer   10.100.xxx.xxx   <external-ip>    3000:xxxxx/TCP   3m
postgres   ClusterIP      10.100.xxx.xxx   <none>           5432/TCP         3m
```

### Step 8: Access the Application

Once the LoadBalancer services have external IPs assigned:

- **Frontend**: `http://<frontend-external-ip>:3000`
- **Backend API**: `http://<backend-external-ip>:8080`

You can test the backend API directly:

```bash
# Health check (should return 401 Unauthorized which means it's working)
curl http://<backend-external-ip>:8080/api/products

# Register a new user
curl -X POST http://<backend-external-ip>:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123"}'
```

## Troubleshooting

### Pods Not Starting

Check pod logs:

```bash
kubectl logs -n stock-app <pod-name>
```

For backend pods:
```bash
kubectl logs -n stock-app -l app=backend
```

For frontend pods:
```bash
kubectl logs -n stock-app -l app=frontend
```

### Database Connection Issues

Check if PostgreSQL is running:

```bash
kubectl get pods -n stock-app -l app=postgres
kubectl logs -n stock-app postgres-0
```

Test database connectivity from backend pod:

```bash
# Get a backend pod name
POD=$(kubectl get pods -n stock-app -l app=backend -o jsonpath='{.items[0].metadata.name}')

# Check if it can reach postgres
kubectl exec -n stock-app $POD -- nc -zv postgres.stock-app.svc.cluster.local 5432
```

### LoadBalancer Pending

If external IPs show `<pending>` for a long time:

```bash
kubectl describe svc -n stock-app frontend
kubectl describe svc -n stock-app backend
```

**Common causes:**
- Your cluster doesn't support LoadBalancer services (e.g., local clusters)
- Cloud provider LoadBalancer provisioning takes time (AWS ELB can take 3-5 minutes)

**Workaround for local clusters (Minikube, Kind, etc.):**

Change service type to NodePort in `terraform/backend.tf` and `terraform/frontend.tf`:

```hcl
# Change from:
type = "LoadBalancer"

# To:
type = "NodePort"
```

Then access via NodePort:
```bash
kubectl get svc -n stock-app
# Use node IP + NodePort to access
```

### Image Pull Errors

If you see `ImagePullBackOff`:

```bash
kubectl describe pod -n stock-app <pod-name>
```

**Solutions:**
- Ensure images are pushed to the registry
- For private registries, create an image pull secret:

```bash
kubectl create secret docker-registry regcred \
  --docker-server=<your-registry-server> \
  --docker-username=<your-username> \
  --docker-password=<your-password> \
  -n stock-app
```

Then add to deployments:
```hcl
spec {
  template {
    spec {
      image_pull_secrets {
        name = "regcred"
      }
      # ... rest of spec
    }
  }
}
```

## Updating the Application

### Update Docker Images

1. Build new images with updated code
2. Tag with new version:
   ```bash
   docker tag stock-app-backend:latest <registry>/stock-app-backend:v1.1
   docker push <registry>/stock-app-backend:v1.1
   ```

3. Update `terraform.tfvars`:
   ```hcl
   backend_image = "<registry>/stock-app-backend:v1.1"
   ```

4. Apply changes:
   ```bash
   terraform apply
   ```

### Rolling Update

Terraform will perform a rolling update, replacing pods gradually to avoid downtime.

Monitor the rollout:
```bash
kubectl rollout status deployment/backend -n stock-app
kubectl rollout status deployment/frontend -n stock-app
```

## Scaling

### Manual Scaling

Update replica counts in `terraform.tfvars`:

```hcl
backend_replicas  = 3
frontend_replicas = 3
```

Apply changes:
```bash
terraform apply
```

### Quick Scaling (without Terraform)

```bash
kubectl scale deployment backend --replicas=3 -n stock-app
kubectl scale deployment frontend --replicas=3 -n stock-app
```

**Note:** If you scale manually, Terraform will reset to the configured count on next apply.

## Data Backup (Important!)

> **⚠️ Critical:** The database uses ephemeral storage. Back up your data regularly or use a managed database for production.

### Backup PostgreSQL Data

```bash
# Get postgres pod name
POD=$(kubectl get pods -n stock-app -l app=postgres -o jsonpath='{.items[0].metadata.name}')

# Backup database
kubectl exec -n stock-app $POD -- pg_dump -U stockapp stockapp > backup.sql
```

### Restore PostgreSQL Data

```bash
# Get postgres pod name
POD=$(kubectl get pods -n stock-app -l app=postgres -o jsonpath='{.items[0].metadata.name}')

# Copy backup to pod
kubectl cp backup.sql stock-app/$POD:/tmp/backup.sql

# Restore
kubectl exec -n stock-app $POD -- psql -U stockapp stockapp < /tmp/backup.sql
```

## Cleanup

To remove all resources:

```bash
cd terraform
terraform destroy
```

Type `yes` when prompted.

**Warning:** This will delete all data including the database. Make sure to backup first!

## Production Considerations

For production deployments, consider:

1. **SSL/TLS**: Use Ingress with cert-manager for HTTPS
2. **Managed Database**: Use RDS, Cloud SQL, or Azure Database instead of in-cluster PostgreSQL
3. **Secrets Management**: Use external secret managers (Vault, AWS Secrets Manager)
4. **Monitoring**: Add Prometheus and Grafana for monitoring
5. **Logging**: Configure centralized logging (ELK stack, CloudWatch)
6. **Resource Limits**: Fine-tune CPU/memory requests and limits
7. **Network Policies**: Restrict pod-to-pod communication
8. **Pod Disruption Budgets**: Ensure availability during updates
9. **Horizontal Pod Autoscaling**: Auto-scale based on CPU/memory
10. **Multi-AZ Deployment**: Spread across availability zones

## Resources

- [Terraform Kubernetes Provider Documentation](https://registry.terraform.io/providers/hashicorp/kubernetes/latest/docs)
- [Kubernetes Documentation](https://kubernetes.io/docs/home/)
- [kubectl Cheat Sheet](https://kubernetes.io/docs/reference/kubectl/cheatsheet/)

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Review Kubernetes pod logs
3. Verify Terraform outputs
4. Check cluster events: `kubectl get events -n stock-app --sort-by='.lastTimestamp'`
