# Quick Start Guide

> **For Analysts and Developers** - Get started in 5 minutes

## 🚀 Quick Setup (One Command)

```bash
# Clone and setup
git clone https://github.com/sovavibe/start.git
cd start
make setup
```

That's it! The `make setup` command will:
- ✅ Check and install Java 21 (if needed)
- ✅ Install all dependencies
- ✅ Configure Git hooks
- ✅ Compile the project

## 📋 Essential Commands

### For Analysts

```bash
make setup          # Setup project
make info           # Show project info
make analyze-full   # Check code quality
make coverage       # View test coverage
```

### For Developers

```bash
make format         # Format code
make analyze-full   # Check quality
make test           # Run tests
make run            # Start application
```

### For Reviewers

```bash
gh pr checkout <number>  # Checkout PR
make analyze-full        # Verify quality
make test               # Run tests
```

## 🎯 Common Workflows

### Starting Development

```bash
# 1. Create branch
git checkout -b feat/scope-description

# 2. Make changes
# ... edit code ...

# 3. Format and check
make format
make analyze-full

# 4. Test
make test

# 5. Create PR
./scripts/create-pr.sh
```

### Before Committing

```bash
make format          # Format code
make format-check    # Verify formatting
make analyze-full    # Check quality
make test            # Run tests
```

### Full CI Pipeline (Local)

```bash
make ci              # Run everything
```

## 📚 Next Steps

- **Analysts**: See [SDLC - Analysis Phase](development/SDLC.md#phase-0-hypothesis--analysis)
- **Developers**: See [SDLC - Development Phase](development/SDLC.md#phase-1-development-preparation)
- **Reviewers**: See [SDLC - Review Phase](development/SDLC.md#phase-4-code-review)

## 🆘 Troubleshooting

| Problem | Solution |
|---------|----------|
| `make setup` fails | Check Java 21 is installed |
| Tests fail | Ensure Docker is running |
| Format check fails | Run `make format` |
| Quality check fails | See [Quality Gates](quality/QUALITY_GATES.md) |

## 📖 Full Documentation

- [Setup Guide](getting-started/SETUP.md) - Detailed setup
- [SDLC Process](development/SDLC.md) - Complete workflow
- [Quality Gates](quality/QUALITY_GATES.md) - Quality standards
- [CI/CD Pipeline](ci-cd/CI_CD.md) - CI/CD details

