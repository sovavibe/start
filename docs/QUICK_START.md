# Quick Start Guide

> Get started in 5 minutes

## Quick Setup

```bash
git clone https://github.com/sovavibe/start.git
cd start
make setup
make postgres-up
make run
```

Access: http://localhost:8080 (admin/admin)

## For Analysts

```bash
./scripts/analyst-workflow.sh
```

Automated workflow: setup → analysis → branch → commit → PR

## Essential Commands

```bash
make setup          # Setup project
make run            # Run application
make test           # Run tests
make format         # Format code
make analyze-full   # Check quality
make ci             # Full CI pipeline
```

## Common Workflows

### Starting Development

```bash
git checkout -b feat/scope-description
# ... make changes ...
make format && make analyze-full && make test
./scripts/create-pr.sh
```

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `make setup` fails | Check Java 21 is installed |
| Tests fail | Ensure Docker is running |
| Format fails | Run `make format` |
| Quality fails | See [Quality Gates](quality/QUALITY_GATES.md) |

## Documentation

- [Setup Guide](getting-started/SETUP.md) - Detailed setup
- [SDLC Process](workflow/SDLC.md) - Complete workflow
- [Quality Gates](quality/QUALITY_GATES.md) - Quality standards
- [Roles Guide](ROLES.md) - Role-specific commands
