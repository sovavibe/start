# DevOps Guide

> **Note**: This guide covers DevOps and deployment. For general project documentation, see [README.md](../README.md). For local development setup, see [Local Development Guide](docs/getting-started/LOCAL_DEVELOPMENT.md).

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
# 1. Create .env file from example (if not exists)
cp .env.example .env

# 2. Update .env file with your values (optional, defaults work for local dev)

# 3. Start services
docker-compose up -d
```

Access application: http://localhost:8080

**Note**: Docker Compose automatically loads variables from `.env` file. You can override values in `.env` or use environment variables directly.

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

- **default**: PostgreSQL (local development with .env file)
- **docker**: PostgreSQL via Docker Compose
- **kubernetes**: PostgreSQL in Kubernetes
- **prod**: Production optimizations

### Environment Variables (.env file)

The application uses `.env` file for local development configuration. All environments use PostgreSQL database.

#### Setup .env file

1. Copy the example file:
   ```bash
   cp .env.example .env
   ```

2. Update values in `.env` file for your environment:
   ```bash
   # Database Configuration
   MAIN_DATASOURCE_URL=jdbc:postgresql://localhost:5432/start
   MAIN_DATASOURCE_USERNAME=start
   MAIN_DATASOURCE_PASSWORD=your-secure-password
   
   # Application Configuration
   SPRING_PROFILES_ACTIVE=default
   SERVER_PORT=8080
   ```

3. The `.env` file is ignored by git (see `.gitignore`)

#### Environment Variables Reference

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `MAIN_DATASOURCE_URL` | Database URL | `jdbc:postgresql://localhost:5432/start` | No |
| `MAIN_DATASOURCE_USERNAME` | Database username | `start` | No |
| `MAIN_DATASOURCE_PASSWORD` | Database password | `start` | **Yes** (change in production!) |
| `SPRING_PROFILES_ACTIVE` | Spring profiles | `default` | No |
| `SERVER_PORT` | Server port | `8080` | No |
| `LOGGING_LEVEL_ROOT` | Root logging level | `INFO` | No |
| `LOGGING_LEVEL_COM_DIGTP_START` | Application logging level | `INFO` | No |
| `MANAGEMENT_OTLP_LOGGING_ENDPOINT` | OpenTelemetry endpoint | `http://otel-collector:4318/v1/logs` | No |

#### Variable Precedence

Environment variables are loaded in the following order (later values override earlier):
1. System environment variables
2. `.env` file (loaded by DotenvConfig)
3. Default values in `application.properties`

**Note**: System environment variables always take precedence over `.env` file values.

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

