# Roles Guide

Quick reference for Analysts, Developers, and Reviewers.

## Analyst

**Role**: Analyze requirements, plan implementation, validate feasibility

### Automated Workflow

```bash
./scripts/analyst-workflow.sh
```

Automated: setup → analysis → branch → commit → PR

### Manual Commands

```bash
make setup          # Setup project
make analyze-full   # Check quality baseline
make coverage       # View coverage
```

**See**: [SDLC - Analysis Phase](workflow/SDLC.md#phase-0-hypothesis--analysis)

## Developer

**Role**: Implement features, write tests, maintain code quality

### Essential Commands

```bash
make format         # Format code
make analyze-full   # Check quality
make test           # Run tests
make ci             # Full CI pipeline
./scripts/create-pr.sh  # Create PR
```

**See**: [SDLC - Development Phase](workflow/SDLC.md#phase-1-development-preparation)

## Reviewer

**Role**: Review code, ensure quality, approve PRs

### Essential Commands

```bash
gh pr checkout <number>  # Checkout PR
make analyze-full        # Verify quality
make test               # Run tests
```

**See**: [SDLC - Review Phase](workflow/SDLC.md#phase-4-code-review)

## Team Lead

**Role**: Manage PRs, assign reviewers, ensure quality

### Essential Commands

```bash
gh pr list              # List PRs
gh pr edit <number> --add-reviewer @username  # Assign reviewer
gh pr review <number>   # Review PR
```

## Documentation

- [Quick Start Guide](QUICK_START.md)
- [SDLC Process](workflow/SDLC.md)
- [Quality Gates](quality/QUALITY_GATES.md)
