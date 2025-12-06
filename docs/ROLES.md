# Roles Guide

Quick reference for Analysts, Developers, and Reviewers.

## Configuring Team Role in Cursor

Cursor supports Team Role configuration to customize AI assistant behavior based on your role in the team.

### Setup via Cursor Settings

1. Open Cursor Settings (Cmd/Ctrl + ,)
2. Navigate to **Features** → **Team Role**
3. Select your role:
   - **Analyst**: Focus on analysis, requirements, and planning
   - **Developer**: Focus on implementation, testing, and code quality
   - **Reviewer**: Focus on code review, quality checks, and PR validation
   - **Team Lead**: Focus on PR management and team coordination

### Setup via Configuration File

You can also configure team role by creating a `.cursorrules` file in the project root or by using the existing rules in `.cursor/rules/`.

The AI assistant will automatically adapt its behavior based on:
- The role selected in Cursor settings
- The rules defined in `.cursor/rules/core.mdc` and other rule files
- The context of your current task

### Role-Specific Behaviors

**Analyst**: 
- Prioritizes requirements analysis and technical planning
- Focuses on feasibility and architecture decisions
- Emphasizes documentation and planning

**Developer**:
- Prioritizes implementation and code quality
- Runs quality checks (`make analyze-full`) automatically
- Focuses on tests and production-ready code

**Reviewer**:
- Prioritizes code review and quality validation
- Checks for compliance with project standards
- Validates tests and quality gates

**Team Lead**:
- Manages PR workflows and team coordination
- Ensures quality gates are met
- Coordinates between team members

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
