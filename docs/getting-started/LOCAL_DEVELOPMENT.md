# Local Development Guide

> **Note**: This guide covers local development workflow. For quick setup, see [Quick Start Guide](../QUICK_START.md). For detailed setup, see [Setup Guide](SETUP.md).

This guide helps you develop the Start project locally.

## Prerequisites

### Required Software

| Software | Version | Download | Documentation |
|----------|---------|----------|---------------|
| **Java** | 21 | [Adoptium](https://adoptium.net/) | [Java 21 Docs](https://docs.oracle.com/en/java/javase/21/) |
| **Docker** | Latest | [Docker Desktop](https://docs.docker.com/get-docker/) | [Docker Docs](https://docs.docker.com/) |
| **Git** | Latest | [Git Downloads](https://git-scm.com/downloads) | [Git Docs](https://git-scm.com/doc) |

### Optional Software

| Software | Purpose | Download |
|----------|---------|----------|
| **Node.js** | Auto-installed by Gradle | [Node.js](https://nodejs.org/) |
| **IntelliJ IDEA** | Recommended IDE | [IntelliJ IDEA](https://www.jetbrains.com/idea/) |
| **VS Code** | Alternative IDE | [VS Code](https://code.visualstudio.com/) |

## Quick Start

### 1. Clone Repository

```bash
git clone https://github.com/sovavibe/start.git
cd start
```

### 2. Setup Project

```bash
make setup
```

This command will:
- Check Java version (must be 21+)
- Verify Gradle wrapper
- Install Gradle dependencies
- Install npm dependencies (Node.js auto-installed if needed)
- Configure Git hooks
- Compile the project

### 3. Start PostgreSQL

```bash
make postgres-up
```

Or use Docker Compose:

```bash
make docker-up
```

### 4. Run Application

```bash
make run
```

Access the application at: http://localhost:8080

**Default credentials** (development only):
- Username: `admin`
- Password: `admin`

## Manual Setup

If you prefer manual setup or `make setup` fails:

### 1. Install Java 21

**macOS (Homebrew)**:
```bash
brew install openjdk@21
```

**Linux (apt)**:
```bash
sudo apt update
sudo apt install openjdk-21-jdk
```

**Windows**:
- Download from [Adoptium](https://adoptium.net/)
- Install and set `JAVA_HOME`

**Verify**:
```bash
java -version
# Should show: openjdk version "21" or similar
```

### 2. Install Docker

**macOS/Windows**:
- Download [Docker Desktop](https://docs.docker.com/get-docker/)
- Install and start Docker Desktop

**Linux**:
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install docker.io docker-compose
sudo systemctl start docker
sudo systemctl enable docker
```

**Verify**:
```bash
docker --version
docker-compose --version
```

### 3. Setup Gradle

Gradle wrapper is included. Make it executable:

```bash
chmod +x gradlew
```

**Verify**:
```bash
./gradlew --version
```

### 4. Install Dependencies

```bash
# Gradle dependencies
./gradlew tasks --console=plain

# npm dependencies (Node.js auto-installed by Gradle)
./gradlew npmInstall
```

### 5. Configure Environment

Create `.env` file (if not exists):

```bash
cp .env.example .env  # If example exists
# Or create manually
```

**Basic `.env` file**:
```bash
# Database Configuration
MAIN_DATASOURCE_URL=jdbc:postgresql://localhost:5432/start
MAIN_DATASOURCE_USERNAME=start
MAIN_DATASOURCE_PASSWORD=start

# Application Configuration
SPRING_PROFILES_ACTIVE=default
SERVER_PORT=8080
```

### 6. Start PostgreSQL

**Option 1: Docker Compose** (Recommended)
```bash
docker-compose -f docker-compose.dev.yml up -d postgres
```

**Option 2: Standalone Docker**
```bash
docker run -d --name start-postgres \
  -e POSTGRES_DB=start \
  -e POSTGRES_USER=start \
  -e POSTGRES_PASSWORD=start \
  -p 5432:5432 \
  postgres:16-alpine
```

**Option 3: Local PostgreSQL**
- Install PostgreSQL 16
- Create database: `createdb start`
- Create user: `createuser start`

**Verify**:
```bash
# Check if PostgreSQL is running
pg_isready -h localhost -p 5432

# Or check Docker
docker ps | grep postgres
```

### 7. Run Application

```bash
./gradlew bootRun
```

Or use Make:

```bash
make run
```

## Development Workflow

### Running Tests

```bash
# Run all tests
make test

# Run with coverage
make coverage

# Open coverage report
make coverage  # Opens in browser automatically (macOS/Linux)
```

**Note**: Tests require Docker for Testcontainers.

### Code Quality Checks

```bash
# Run all quality checks
make analyze-full

# Format code
make format

# Check formatting only
make format-check
```

### Building

```bash
# Build application
make build

# Build JAR only
./gradlew bootJar
```

### Common Commands

```bash
# Setup (first time)
make setup

# Run application
make run

# Run tests
make test

# Check quality
make analyze-full

# Format code
make format

# Full CI pipeline locally
make ci

# Clean build
make clean
```

See [Makefile](../Makefile) for all available commands.

## IDE Setup

### IntelliJ IDEA

1. **Open Project**
   - File → Open → Select project directory
   - Import as Gradle project

2. **Configure JDK**
   - File → Project Structure → Project
   - Set SDK to Java 21

3. **Enable Annotation Processing**
   - File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - Enable annotation processing

4. **Install Plugins** (Recommended)
   - SonarLint
   - Checkstyle-IDEA
   - Lombok

5. **Code Style**
   - Palantir Baseline automatically configures code style
   - Use `./gradlew format` for formatting

### VS Code

1. **Install Extensions**
   - Extension Pack for Java
   - SonarLint
   - Checkstyle for Java
   - Lombok Annotations Support

2. **Configure Java**
   - Set `java.home` in settings
   - Point to Java 21 installation

3. **Code Formatting**
   - Use `./gradlew format` for formatting
   - Or configure VS Code to use Spotless

## Environment Configuration

### Environment Variables

The application uses environment variables with defaults. Priority:

1. System environment variables (highest)
2. `.env` file
3. `application.properties` defaults (lowest)

### Common Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `MAIN_DATASOURCE_URL` | Database URL | `jdbc:postgresql://localhost:5432/start` |
| `MAIN_DATASOURCE_USERNAME` | Database username | `start` |
| `MAIN_DATASOURCE_PASSWORD` | Database password | `start` |
| `SPRING_PROFILES_ACTIVE` | Spring profiles | `default` |
| `SERVER_PORT` | Server port | `8080` |
| `LOGGING_LEVEL_ROOT` | Root logging level | `INFO` |

### Profiles

- **default**: Local development with `.env` file
- **docker**: Docker Compose environment
- **kubernetes**: Kubernetes deployment
- **prod**: Production optimizations

## Database Setup

### Using Docker Compose

```bash
# Start PostgreSQL
docker-compose -f docker-compose.dev.yml up -d postgres

# View logs
docker-compose -f docker-compose.dev.yml logs -f postgres

# Stop PostgreSQL
docker-compose -f docker-compose.dev.yml down postgres
```

### Using Make

```bash
# Start PostgreSQL
make postgres-up

# Stop PostgreSQL
make postgres-down

# View logs
make postgres-logs
```

### Database Migrations

Migrations are managed by Liquibase:
- Location: `src/main/resources/com/digtp/start/liquibase/`
- Applied automatically on startup
- Never modify applied migrations

## Troubleshooting

### Java Version Issues

**Problem**: Wrong Java version

**Solution**:
```bash
# Check version
java -version

# Set JAVA_HOME (macOS/Linux)
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Verify
echo $JAVA_HOME
```

### Docker Issues

**Problem**: Docker not running

**Solution**:
```bash
# Start Docker Desktop (macOS/Windows)
# Or start Docker service (Linux)
sudo systemctl start docker

# Verify
docker ps
```

**Problem**: Testcontainers not working

**Solution**:
- Ensure Docker is running
- Check Docker socket permissions (Linux)
- Verify `TESTCONTAINERS_REUSE_ENABLE=false` in CI

### Database Connection Issues

**Problem**: Cannot connect to PostgreSQL

**Solution**:
```bash
# Check if PostgreSQL is running
pg_isready -h localhost -p 5432

# Check Docker container
docker ps | grep postgres

# Check logs
docker logs start-postgres
# Or
make postgres-logs
```

**Problem**: Database doesn't exist

**Solution**:
```bash
# Create database manually
docker exec -it start-postgres psql -U start -c "CREATE DATABASE start;"
```

### Port Already in Use

**Problem**: Port 8080 already in use

**Solution**:
```bash
# Kill process on port 8080
make kill-port PORT=8080

# Or manually
lsof -ti:8080 | xargs kill -9
```

### Gradle Issues

**Problem**: Gradle build fails

**Solution**:
```bash
# Clean and rebuild
./gradlew clean build

# Clear Gradle cache
rm -rf ~/.gradle/caches/

# Re-download dependencies
./gradlew --refresh-dependencies
```

### Node.js Issues

**Problem**: Node.js not found

**Solution**:
- Node.js is auto-installed by Gradle
- If issues persist, install manually:
  ```bash
  # macOS
  brew install node@24

  # Or let Gradle install it
  ./gradlew npmInstall
  ```

### Quality Check Failures

**Problem**: Quality checks fail locally

**Solution**:
```bash
# Format code first
make format

# Run checks again
make analyze-full

# Fix reported issues
# See [Quality Gates Documentation](QUALITY_GATES.md)
```

### Test Failures

**Problem**: Tests fail

**Solution**:
```bash
# Ensure Docker is running
docker ps

# Run tests with verbose output
./gradlew test --info

# Run specific test
./gradlew test --tests "com.digtp.start.*Test"

# Check Testcontainers logs
# See test output for details
```

## Development Tips

### Hot Reload

Vaadin supports hot reload in development mode:
- Changes to Java files trigger reload
- Browser automatically refreshes
- No manual restart needed

### Debugging

**IntelliJ IDEA**:
1. Create Run Configuration
2. Type: Application
3. Main class: `com.digtp.start.StartApplication`
4. VM options: `-Dspring.profiles.active=default`
5. Debug (Shift+F9)

**VS Code**:
1. Install Java Debug extension
2. Create `launch.json`
3. Debug configuration for Spring Boot

### Logging

**View Logs**:
```bash
# Application logs
tail -f logs/application.log

# Error logs
tail -f logs/errors.log

# SQL logs
tail -f logs/sql.log
```

**Change Log Level**:
- Edit `application.properties`
- Or set environment variable: `LOGGING_LEVEL_COM_DIGTP_START=DEBUG`

### Database Access

**Connect to PostgreSQL**:
```bash
# Using psql
psql -h localhost -U start -d start

# Using Docker
docker exec -it start-postgres psql -U start -d start
```

**View Database Schema**:
- Use Jmix Studio (if installed)
- Or use database tools (DBeaver, pgAdmin)

## Next Steps

- Read [Architecture Documentation](ARCHITECTURE.md)
- Review [Quality Gates Documentation](QUALITY_GATES.md)
- Check [CI/CD Documentation](CI_CD.md)
- See [Contributing Guide](../CONTRIBUTING.md)

## References

- [Java 21 Documentation](https://docs.oracle.com/en/java/javase/21/)
- [Jmix Documentation](https://docs.jmix.io/jmix/)
- [Vaadin Documentation](https://vaadin.com/docs)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Docker Documentation](https://docs.docker.com/)
- [Gradle User Guide](https://docs.gradle.org/8.12/userguide/userguide.html)

