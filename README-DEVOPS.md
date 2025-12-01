# DevOps Guide

## Quick Start

### 1. Start Application

```bash
# Using Make
make docker-up

# Or directly
docker-compose up -d
```

Access application: http://localhost:8080

### 2. Start Observability Stack

```bash
# Using Make
make observability-up

# Or directly
docker-compose -f docker-compose.observability.yml up -d
```

Access Grafana: http://localhost:3000 (admin/admin)

### 3. Check Status

```bash
# Check all services
make check-devops

# Or use script directly
./scripts/check-devops.sh
```

### 4. Test Observability

```bash
# Test observability stack
make test-observability

# Or use script directly
./scripts/test-observability.sh
```

## Docker Compose

### Local Development

Start application with PostgreSQL:

```bash
docker-compose up -d
```

Access application: http://localhost:8080

### Observability Stack

Start observability stack (Grafana + Loki + OpenTelemetry Collector):

```bash
docker-compose -f docker-compose.observability.yml up -d
```

Access Grafana: http://localhost:3000 (admin/admin)

## Kubernetes

### Deploy with kubectl

```bash
# Create namespace
kubectl apply -f k8s/namespace.yaml

# Create secrets (update password!)
kubectl apply -f k8s/secret.yaml

# Deploy PostgreSQL
kubectl apply -f k8s/postgres-pvc.yaml
kubectl apply -f k8s/postgres-deployment.yaml
kubectl apply -f k8s/postgres-service.yaml

# Deploy application
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/app-deployment.yaml
kubectl apply -f k8s/app-service.yaml

# Deploy ingress (optional)
kubectl apply -f k8s/ingress.yaml
```

### Deploy with Helm

```bash
# Install
helm install start ./helm/start

# Upgrade
helm upgrade start ./helm/start

# Uninstall
helm uninstall start
```

### Deploy with Kustomize

```bash
kubectl apply -k k8s/
```

## GitHub Actions

### CI/CD Pipeline

- **Build and Test**: Runs on every push/PR
- **Security Checks**: OWASP + Trivy scanning
- **Docker Build**: Builds and pushes images on main/develop branches

### Release Workflow

Create a release tag:

```bash
git tag v1.0.0
git push origin v1.0.0
```

This triggers:
- Docker image build with version tag
- GitHub Release creation

## Configuration

### Profiles

- **default**: HSQLDB (local development)
- **docker**: PostgreSQL via Docker Compose
- **kubernetes**: PostgreSQL in Kubernetes
- **prod**: Production optimizations

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `MAIN_DATASOURCE_URL` | Database URL | `jdbc:postgresql://postgres:5432/start` |
| `MAIN_DATASOURCE_USERNAME` | Database username | `start` |
| `MAIN_DATASOURCE_PASSWORD` | Database password | (required) |
| `MANAGEMENT_OTLP_LOGGING_ENDPOINT` | OpenTelemetry endpoint | `http://otel-collector:4318/v1/logs` |

## Observability

### OpenTelemetry

Logs are automatically sent to OpenTelemetry Collector when:
- `management.otlp.logging.endpoint` is configured
- Profile is `docker` or `kubernetes`

### Grafana

View logs in Grafana:
1. Open http://localhost:3000
2. Go to Explore
3. Select Loki datasource
4. Query: `{service_name="start"}`

## Secrets Management

### Kubernetes

Update secrets:

```bash
kubectl create secret generic start-secret \
  --from-literal=MAIN_DATASOURCE_PASSWORD=your-password \
  --dry-run=client -o yaml | kubectl apply -f -
```

### Helm

Override in `values.yaml` or use `--set`:

```bash
helm install start ./helm/start \
  --set secrets.mainDatasourcePassword=your-password
```

