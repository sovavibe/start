# Start - Vibe Coding Jmix Project

[![CI/CD Pipeline](https://github.com/sovavibe/start/workflows/CI/CD%20Pipeline/badge.svg)](https://github.com/sovavibe/start/actions)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://docs.oracle.com/en/java/javase/21/)
[![Jmix](https://img.shields.io/badge/Jmix-2.7.1-blue.svg)](https://docs.jmix.io/jmix/)

Production-ready Jmix application template demonstrating **Vibe Coding** philosophy: strict quality gates, minimal suppressions, senior-level code standards.

## Vibe Coding

- **Minimal Suppressions, Maximum Quality**: Fix root causes, not symptoms
- **Strict Quality Gates**: All checks must pass (`ignoreFailures=false`)
- **Production-Ready from Day One**: Senior+ level code standards
- **Framework-Specific Suppressions Only**: Suppress only for Jmix/Vaadin/Lombok false positives

## Quick Start

```bash
git clone https://github.com/sovavibe/start.git
cd start
make setup
make postgres-up
make run
```

Access: http://localhost:8080 (admin/admin)

For analysts: `./scripts/analyst-workflow.sh` (fully automated)

## Tech Stack

- **Java 21**, **Jmix 2.7.1**, **Vaadin 24+**, **Spring Boot 3.x**
- **PostgreSQL 16**, **Liquibase**, **EclipseLink**
- **Quality Tools**: Checkstyle, PMD, SpotBugs, SonarLint, Error-prone, JaCoCo
- **CI/CD**: GitHub Actions, SonarLint (via Gradle)
- **Observability**: OpenTelemetry, Grafana, Loki

## Quality Gates

- **Coverage**: 75% instructions, 65% branches, 75% lines
- **Complexity**: ≤10 cognitive/cyclomatic per method
- **File Length**: ≤250 lines
- **Duplication**: <3%
- **All checks**: Must pass (`ignoreFailures=false`)

See [Quality Gates](docs/quality/QUALITY_GATES.md) for details.

## Documentation

### Quick Links

- **[Quick Start](docs/QUICK_START.md)** - Get started in 5 minutes
- **[Setup Guide](docs/getting-started/SETUP.md)** - Detailed setup
- **[SDLC](docs/workflow/SDLC.md)** - Development workflow
- **[Quality Gates](docs/quality/QUALITY_GATES.md)** - Quality standards
- **[Architecture](docs/architecture/ARCHITECTURE.md)** - System design

### Documentation Structure

```
docs/
├── QUICK_START.md                    # 5-minute quick start
├── ROLES.md                           # Role-specific commands
├── getting-started/
│   └── SETUP.md                       # Setup instructions
├── architecture/
│   └── ARCHITECTURE.md                # System architecture (C4)
├── workflow/
│   ├── SDLC.md                        # Development workflow
│   └── ANALYST_WORKFLOW_EXAMPLE.md    # Workflow example
├── quality/
│   ├── QUALITY_GATES.md               # Quality thresholds
│   ├── CI_CD.md                       # CI/CD pipeline
│   ├── SUPPRESSIONS.md                # Suppression patterns
│   └── SUPPRESSION_CENTRALIZATION_PLAN.md
└── open-source/
    ├── PUBLICATION_CHECKLIST.md       # Publication checklist
    └── SONARQUBE_TODO.md              # SonarQube integration plan
```

**[Full Documentation Index](docs/README.md)** - Complete documentation

## Commands

```bash
make setup          # Setup project
make run            # Run application
make test           # Run tests
make format         # Format code
make analyze-full   # Check quality
make ci             # Full CI pipeline
```

## Contributing

See [Contributing Guide](CONTRIBUTING.md) for:
- Development setup
- Code quality standards
- Pull request process
- Vibe Coding principles

## License

Apache License 2.0 - see [LICENSE](LICENSE) file.

Copyright © 2025 Digital Technologies and Platforms LLC.
