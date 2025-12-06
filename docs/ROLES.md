# Roles Guide

Quick reference for Analysts, Developers, and Reviewers.

## Cursor Configuration

This project uses Cursor's rule system for AI assistance.

### Configuration Files

- **`.cursor/rules/*.mdc`**: Configuration files (Cursor reads these)
  - 8 rule files with YAML frontmatter
  - `core.mdc` always loaded (`alwaysApply: true`)
  - Other rules load based on `globs` patterns when editing matching files
- **`.cursor/BUGBOT.md`**: Bugbot PR review guidelines

### How It Works

1. **Rules Load Automatically**: Based on file patterns in YAML frontmatter
2. **Core Always**: `core.mdc` contains project standards and is always loaded
3. **Context Rules**: Other rules load when editing files matching their patterns
4. **Bugbot**: Uses `.cursor/BUGBOT.md` for automatic PR reviews

See [Cursor Configuration](CURSOR_CONFIGURATION.md) for details.

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
