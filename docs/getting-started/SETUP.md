# Setup Guide

> **Quick Start**: Run `make setup` - it does everything automatically!

## Prerequisites

- **Java 21** - [Download](https://adoptium.net/)
- **Docker** - [Download](https://docs.docker.com/get-docker/)
- **Git** - [Download](https://git-scm.com/downloads)

## Quick Setup

```bash
git clone https://github.com/sovavibe/start.git
cd start
make setup
make postgres-up
make run
```

Access: http://localhost:8080 (admin/admin)

## Manual Setup

### 1. Install Java 21

**macOS**: `brew install openjdk@21`  
**Linux**: `sudo apt install openjdk-21-jdk`  
**Windows**: Download from [Adoptium](https://adoptium.net/)

Verify: `java -version`

### 2. Install Docker

**macOS/Windows**: Download [Docker Desktop](https://docs.docker.com/get-docker/)  
**Linux**: `sudo apt install docker.io docker-compose && sudo systemctl start docker`

Verify: `docker --version`

### 3. Setup Project

```bash
chmod +x gradlew
./gradlew npmInstall
```

### 4. Configure Environment

Create `.env` file:

```bash
MAIN_DATASOURCE_URL=jdbc:postgresql://localhost:5432/start
MAIN_DATASOURCE_USERNAME=start
MAIN_DATASOURCE_PASSWORD=start
SPRING_PROFILES_ACTIVE=default
SERVER_PORT=8080
```

### 5. Start PostgreSQL

```bash
make postgres-up
# Or: docker-compose -f docker-compose.dev.yml up -d postgres
```

### 6. Run Application

```bash
make run
# Or: ./gradlew bootRun
```

## IDE Setup

### IntelliJ IDEA

1. Open project → Import as Gradle project
2. Set SDK to Java 21
3. Enable annotation processing
4. Install plugins: SonarLint, Checkstyle-IDEA, Lombok

### VS Code

1. Install: Extension Pack for Java, SonarLint, Checkstyle, Lombok
2. Set `java.home` to Java 21

## Development Commands

```bash
make setup          # Setup project
make run            # Run application
make test           # Run tests
make format         # Format code
make analyze-full   # Check quality
make coverage       # View coverage
make ci             # Full CI pipeline
```

## Troubleshooting

| Problem | Solution |
|---------|----------|
| Wrong Java version | `export JAVA_HOME=$(/usr/libexec/java_home -v 21)` |
| Docker not running | Start Docker Desktop or `sudo systemctl start docker` |
| PostgreSQL not running | `make postgres-up` or check `docker ps` |
| Port 8080 in use | `lsof -ti:8080 | xargs kill -9` |
| Gradle build fails | `./gradlew clean build --refresh-dependencies` |
| Quality checks fail | `make format && make analyze-full` |

## Next Steps

- [Quick Start Guide](../QUICK_START.md)
- [Architecture Documentation](../architecture/ARCHITECTURE.md)
- [Contributing Guide](../../CONTRIBUTING.md)
