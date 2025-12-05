# Security Best Practices for SonarQube Configuration

## Critical Security Rules

### ❌ NEVER Do This

1. **Never hardcode tokens/passwords in Dockerfile**:
   ```dockerfile
   # ❌ BAD - Never do this!
   ENV SONAR_TOKEN=sqa_1234567890
   ENV SONAR_PASSWORD=admin
   ```

2. **Never commit secrets to Git**:
   ```yaml
   # ❌ BAD - Never commit this!
   environment:
     SONAR_TOKEN: sqa_1234567890
     SONAR_PASSWORD: secret123
   ```

3. **Never put secrets in docker-compose.yml**:
   ```yaml
   # ❌ BAD - Never do this!
   services:
     sonarqube:
       environment:
         SONAR_TOKEN: sqa_1234567890
   ```

### ✅ ALWAYS Do This

1. **Use `.env` file (not in Git)**:
   ```bash
   # .env (add to .gitignore!)
   SONAR_USER=admin
   SONAR_PASSWORD=your_secure_password
   SONAR_TOKEN=sqa_1234567890
   ```

2. **Load from environment variables**:
   ```yaml
   # docker-compose.dev.yml
   services:
     sonarqube:
       env_file:
         - .env
       environment:
         SONAR_USER: ${SONAR_USER:-admin}
         SONAR_PASSWORD: ${SONAR_PASSWORD:-admin}
   ```

3. **Dockerfile only copies scripts**:
   ```dockerfile
   # ✅ GOOD - No secrets in Dockerfile
   FROM sonarqube:25.11.0.114957-community
   COPY init-quality-profile.sh /opt/sonarqube/extensions/
   # No ENV with secrets!
   ```

## How Our Implementation Works

### 1. Dockerfile (No Secrets)

```dockerfile
# docker/sonarqube/Dockerfile
FROM sonarqube:25.11.0.114957-community
COPY init-quality-profile.sh /opt/sonarqube/extensions/
# ✅ No secrets here - only scripts
```

### 2. docker-compose.dev.yml (Loads from .env)

```yaml
services:
  sonarqube:
    env_file:
      - .env  # ✅ Loads secrets from .env
    environment:
      SONAR_USER: ${SONAR_USER:-admin}  # ✅ From .env
      SONAR_PASSWORD: ${SONAR_PASSWORD:-admin}  # ✅ From .env
```

### 3. Init Script (Uses Environment Variables)

```bash
# docker/sonarqube/init-quality-profile.sh
SONAR_USER="${SONAR_USER:-admin}"  # ✅ From environment
SONAR_PASSWORD="${SONAR_PASSWORD:-admin}"  # ✅ From environment
# ✅ No hardcoded values
```

### 4. .env File (Not in Git)

```bash
# .env (in .gitignore!)
SONAR_USER=admin
SONAR_PASSWORD=your_secure_password
SONAR_TOKEN=sqa_1234567890
```

## Security Checklist

- ✅ `.env` is in `.gitignore`
- ✅ Dockerfile has no secrets
- ✅ docker-compose.dev.yml uses `${VAR}` syntax
- ✅ All secrets come from environment variables
- ✅ Default values are safe (local dev only)

## Token Usage

### For Quality Profile Initialization

**Uses**: `SONAR_USER` and `SONAR_PASSWORD` (not token)
- These are for API authentication during setup
- Stored in `.env` file
- Passed via `docker-compose.dev.yml`

### For SonarScanner Analysis

**Uses**: `SONAR_TOKEN` (from environment)
- Used by Gradle `sonar` task
- Stored in `.env` file
- Never in Dockerfile or docker-compose.yml

### For IntelliJ IDEA

**Uses**: `SONAR_TOKEN` (manual setup)
- Configured in IntelliJ IDEA settings
- Not used in Docker setup
- Stored locally in IDE

## Best Practices Summary

1. **Secrets in `.env`** (not in Git)
2. **Dockerfile is secret-free** (only scripts)
3. **docker-compose loads from `.env`** (via `env_file`)
4. **Scripts use environment variables** (no hardcoded values)
5. **Default values are safe** (local dev only)

## Production Considerations

For production:
- Use **Docker secrets** or **Kubernetes secrets**
- Use **environment variables** from CI/CD
- Never commit `.env` files
- Rotate tokens regularly
- Use least privilege principle

