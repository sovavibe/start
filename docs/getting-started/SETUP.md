# Setup Guide

> **Quick Start**: Run `make setup` - it does everything automatically!

This guide provides detailed setup instructions. For quick start, see [Quick Start Guide](../QUICK_START.md).

## Prerequisites

Before you begin, ensure you have the following installed:

### Required

- **Java 21** - [Download](https://adoptium.net/) or [Documentation](https://docs.oracle.com/en/java/javase/21/)
- **Docker** - [Download](https://docs.docker.com/get-docker/) or [Documentation](https://docs.docker.com/)
- **Git** - [Download](https://git-scm.com/downloads)

### Optional

- **IntelliJ IDEA** - Recommended IDE - [Download](https://www.jetbrains.com/idea/)
- **Node.js** - Auto-installed by Gradle, but can be installed manually - [Download](https://nodejs.org/)

## Quick Setup

The fastest way to get started:

```bash
# 1. Clone the repository
git clone https://github.com/sovavibe/start.git
cd start

# 2. Run setup (installs everything automatically)
make setup

# 3. Start PostgreSQL
make postgres-up

# 4. Run the application
make run
```

Access the application at: http://localhost:8080

**Default credentials** (development only):
- Username: `admin`
- Password: `admin`

## Detailed Setup

### 1. Install Java 21

**macOS (Homebrew)**:
```bash
brew install openjdk@21
```

**Linux (Ubuntu/Debian)**:
```bash
sudo apt update
sudo apt install openjdk-21-jdk
```

**Windows**:
- Download from [Adoptium](https://adoptium.net/)
- Install and set `JAVA_HOME` environment variable

**Verify installation**:
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
sudo apt update
sudo apt install docker.io docker-compose
sudo systemctl start docker
sudo systemctl enable docker
```

**Verify installation**:
```bash
docker --version
docker-compose --version
```

### 3. Clone Repository

```bash
git clone https://github.com/sovavibe/start.git
cd start
```

### 4. Setup Project

```bash
# Make gradlew executable
chmod +x gradlew

# Install dependencies
./gradlew tasks --console=plain
./gradlew npmInstall
```

### 5. Configure Environment

Create `.env` file in project root:

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

**Using Docker Compose** (Recommended):
```bash
docker-compose -f docker-compose.dev.yml up -d postgres
```

**Using Make**:
```bash
make postgres-up
```

**Verify PostgreSQL is running**:
```bash
pg_isready -h localhost -p 5432
# Or
docker ps | grep postgres
```

### 7. Run Application

```bash
# Using Make
make run

# Or directly
./gradlew bootRun
```

Access at: http://localhost:8080

## IDE Setup

### IntelliJ IDEA

1. **Open Project**:
   - File → Open → Select project directory
   - Import as Gradle project

2. **Configure JDK**:
   - File → Project Structure → Project
   - Set SDK to Java 21

3. **Enable Annotation Processing**:
   - File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - Enable annotation processing

4. **Install Plugins** (Recommended):
   - SonarLint
   - Checkstyle-IDEA
   - Lombok

5. **Code Style**:
   - Palantir Baseline automatically configures code style
   - Use `./gradlew format` for formatting

### VS Code

1. **Install Extensions**:
   - Extension Pack for Java
   - SonarLint
   - Checkstyle for Java
   - Lombok Annotations Support

2. **Configure Java**:
   - Set `java.home` in settings
   - Point to Java 21 installation

## Verification

After setup, verify everything works:

```bash
# Check Java
java -version

# Check Docker
docker ps

# Check PostgreSQL
pg_isready -h localhost -p 5432

# Run tests
make test

# Check code quality
make analyze-full
```

## Troubleshooting

See [Local Development Guide](LOCAL_DEVELOPMENT.md#troubleshooting) for common issues and solutions.

## Next Steps

- Read [Local Development Guide](LOCAL_DEVELOPMENT.md)
- Review [Architecture Documentation](ARCHITECTURE.md)
- Check [Contributing Guide](../CONTRIBUTING.md)

## References

- [Java 21 Documentation](https://docs.oracle.com/en/java/javase/21/)
- [Docker Documentation](https://docs.docker.com/)
- [Jmix Documentation](https://docs.jmix.io/jmix/)
- [Vaadin Documentation](https://vaadin.com/docs)

