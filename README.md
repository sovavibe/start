# Start - Vibe Coding Jmix Project

[![CI/CD Pipeline](https://github.com/sovavibe/start/workflows/CI/CD%20Pipeline/badge.svg)](https://github.com/sovavibe/start/actions)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![SonarCloud Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=sovavibe_start&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=sovavibe_start)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=sovavibe_start&metric=coverage)](https://sonarcloud.io/summary/new_code?id=sovavibe_start)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://docs.oracle.com/en/java/javase/21/)
[![Jmix](https://img.shields.io/badge/Jmix-2.7.1-blue.svg)](https://docs.jmix.io/jmix/)

A production-ready Jmix application template demonstrating **Vibe Coding** philosophy: strict quality gates, minimal suppressions, and senior-level code standards.

## What is Vibe Coding?

**Vibe Coding** is a development philosophy that emphasizes:

- **Minimal Suppressions, Maximum Quality**: Fix root causes, not symptoms
- **Strict Quality Gates**: All checks must pass (`ignoreFailures=false` everywhere)
- **Production-Ready from Day One**: Senior+ level code standards
- **Framework-Specific Suppressions Only**: Suppress only for Jmix/Vaadin/Lombok false positives
- **Token Economy**: Optimize rule usage and documentation

This project serves as a reference implementation for building high-quality Jmix applications with comprehensive quality assurance.

## Quick Start

### Prerequisites

- **Java 21** - [Download](https://adoptium.net/) or [Documentation](https://docs.oracle.com/en/java/javase/21/)
- **Docker** - [Download](https://docs.docker.com/get-docker/) or [Documentation](https://docs.docker.com/)
- **Node.js** (auto-installed by Gradle) - [Documentation](https://nodejs.org/docs/)

### Local Development

```bash
# 1. Clone the repository
git clone https://github.com/sovavibe/start.git
cd start

# 2. Setup project (installs dependencies, configures git hooks)
make setup

# 3. Start PostgreSQL (if not using Docker Compose)
make postgres-up

# 4. Run the application
make run
```

Access the application at: http://localhost:8080

**Default credentials** (development only):
- Username: `admin`
- Password: `admin`

For detailed setup instructions, see [Local Development Guide](docs/getting-started/LOCAL_DEVELOPMENT.md).

## Features

- **Modern Tech Stack**: Java 21, Jmix 2.7.1, Vaadin 24+, Spring Boot 3.x
- **Comprehensive Quality Gates**: Checkstyle, PMD, SpotBugs, SonarLint, Error-prone
- **High Test Coverage**: 85%+ instructions, 75%+ branches, 90%+ lines
- **Security Scanning**: OWASP Dependency-Check, Trivy
- **Observability**: OpenTelemetry, Grafana, Loki
- **CI/CD**: Automated testing, quality checks, and deployment
- **Docker & Kubernetes**: Ready for containerized deployment

## Technology Stack

| Component | Version | Documentation |
|-----------|---------|---------------|
| **Java** | 21 | [Java 21 Docs](https://docs.oracle.com/en/java/javase/21/) |
| **Jmix** | 2.7.1 | [Jmix Documentation](https://docs.jmix.io/jmix/) |
| **Vaadin** | 24+ | [Vaadin Docs](https://vaadin.com/docs) |
| **Spring Boot** | 3.x | [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/) |
| **PostgreSQL** | 16 | [PostgreSQL Docs](https://www.postgresql.org/docs/) |
| **Liquibase** | Latest | [Liquibase Docs](https://docs.liquibase.com/) |
| **Gradle** | 8.12.x | [Gradle User Guide](https://docs.gradle.org/8.12/userguide/userguide.html) |
| **Palantir Baseline** | 6.76.0 | [Baseline GitHub](https://github.com/palantir/gradle-baseline) |
| **EclipseLink** | Latest | [EclipseLink Docs](https://www.eclipse.org/eclipselink/documentation/) |

### Quality Tools

| Tool | Purpose | Documentation |
|------|---------|---------------|
| **Checkstyle** | Code style checking | [Checkstyle](https://checkstyle.sourceforge.io/) |
| **PMD** | Static code analysis | [PMD](https://pmd.github.io/) |
| **SpotBugs** | Bug pattern detection | [SpotBugs](https://spotbugs.github.io/) |
| **SonarLint/SonarCloud** | Code quality & security | [SonarLint](https://www.sonarsource.com/products/sonarlint/), [SonarCloud](https://docs.sonarcloud.io/) |
| **Error-prone** | Compile-time error detection | [Error Prone](https://errorprone.info/) |
| **JaCoCo** | Code coverage | [JaCoCo](https://www.jacoco.org/jacoco/) |
| **Spotless** | Code formatting | [Spotless](https://github.com/diffplug/spotless) |

For comprehensive quality gates documentation, see [Quality Gates Guide](docs/quality/QUALITY_GATES.md).

## Quality Gates

This project enforces strict quality standards:

- **Code Coverage**: 85% instructions, 75% branches, 90% lines
- **Static Analysis**: All checks must pass (`ignoreFailures=false`)
- **Cognitive Complexity**: ≤10 per method
- **Cyclomatic Complexity**: ≤10 per method
- **File Length**: ≤250 lines
- **Code Duplication**: <3%

All quality gates are enforced in CI/CD. See [Quality Gates Documentation](docs/quality/QUALITY_GATES.md) for details.

## Documentation

📚 **[Full Documentation Index](docs/README.md)** - Complete documentation organized by category

### Quick Links

**Getting Started:**
- **[Setup Guide](docs/getting-started/SETUP.md)** - Step-by-step setup instructions
- **[Local Development](docs/getting-started/LOCAL_DEVELOPMENT.md)** - Local setup and development guide

**Architecture & Development:**
- **[Quick Start](docs/QUICK_START.md)** ⭐ - Get started in 5 minutes
- **[Roles Guide](docs/ROLES.md)** ⭐ - Quick reference for Analysts, Developers, Reviewers
- **[Architecture](docs/architecture/ARCHITECTURE.md)** - System architecture and design patterns (C4 Model)
- **[SDLC](docs/development/SDLC.md)** - Software Development Life Cycle for Vibe Coding (with Mermaid diagrams)

**Quality & CI/CD:**
- **[Quality Gates](docs/quality/QUALITY_GATES.md)** - Comprehensive quality gates documentation
- **[CI/CD](docs/ci-cd/CI_CD.md)** - Continuous Integration and Deployment pipeline
- **[CI/CD Setup](docs/ci-cd/CI_CD_SETUP.md)** - CI/CD configuration and secrets setup
- **[SonarCloud Verification](docs/ci-cd/SONARCLOUD_VERIFICATION.md)** - SonarCloud connection verification

**Open Source:**
- **[GitHub Setup](docs/open-source/GITHUB_SETUP.md)** - GitHub repository configuration
- **[GitHub Status](docs/open-source/GITHUB_STATUS.md)** - Current GitHub configuration status
- **[Publication Checklist](docs/open-source/PUBLICATION_CHECKLIST.md)** - Pre-publication verification

**DevOps:**
- **[DevOps Guide](README-DEVOPS.md)** - Docker, Kubernetes, and deployment

## Development Workflow

This project follows Vibe Coding SDLC principles. See [SDLC Documentation](docs/development/SDLC.md) for details.

### Key Commands

```bash
# Setup project
make setup

# Run application
make run

# Run tests
make test

# Check code quality
make analyze-full

# Format code
make format

# Build application
make build

# Full CI pipeline
make ci
```

### 🤖 For Analysts (Fully Automated)

```bash
# Zero knowledge required - just run this!
./scripts/analyst-workflow.sh
```

The script guides you through everything automatically - from hypothesis to PR!

See [Makefile](Makefile) for all available commands.

## Contributing

We welcome contributions! Please read our [Contributing Guide](CONTRIBUTING.md) for details on:

- Development setup
- Code quality standards
- Pull request process
- Vibe Coding principles

By contributing, you agree to follow our [Code of Conduct](CODE_OF_CONDUCT.md).

## Security

If you discover a security vulnerability, please see our [Security Policy](SECURITY.md) for reporting instructions.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

Copyright © 2025 Digital Technologies and Platforms LLC. All rights reserved.

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for a list of changes and version history.

## Acknowledgments

- [Jmix Framework](https://www.jmix.io/) - High-productivity application development platform
- [Palantir Baseline](https://github.com/palantir/gradle-baseline) - Code quality tools
- [Spring Boot](https://spring.io/projects/spring-boot) - Application framework
- [Vaadin](https://vaadin.com/) - Web application framework

## Support

- **Documentation**: See [docs/](docs/) directory
- **Issues**: [GitHub Issues](https://github.com/sovavibe/start/issues)
- **Discussions**: [GitHub Discussions](https://github.com/sovavibe/start/discussions)

## Quick Status

Check GitHub and SonarCloud setup:

```bash
# Check GitHub setup
./scripts/setup-github.sh

# Check SonarCloud connection
./scripts/check-sonarcloud.sh
```

See [GitHub Status](docs/open-source/GITHUB_STATUS.md) for current configuration status.

## Publication Checklist

Before publishing, see [Publication Checklist](docs/open-source/PUBLICATION_CHECKLIST.md) for setup verification.

---

**Built with ❤️ using Vibe Coding principles**

