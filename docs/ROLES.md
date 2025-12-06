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

The project includes:
- **`.cursor/rules/`**: **Primary** configuration system (this is what Cursor uses)
  - 8 rule files with YAML frontmatter (`alwaysApply`, `globs`, `version`)
  - `core.mdc` always loaded, others load based on file patterns
- **`.cursor/BUGBOT.md`**: Code review guidelines for Bugbot
- **`.cursorrules`**: Optional quick reference (NOT used by Cursor for configuration)

The AI assistant will automatically adapt its behavior based on:
- The role selected in Cursor settings
- The rules defined in `.cursor/rules/core.mdc` and other rule files
- The context of your current task (rules load based on file patterns)

### Agent Configuration

Cursor supports different agent types:

#### Background Agents
- **Purpose**: Autonomous code improvements
- **Configuration**: Via Cursor Settings → Features → Background Agents
- **Recommendations**:
  - ✅ Enable for: Formatting, import organization, simple refactorings
  - ⚠️ Disable for: Complex logic changes, architecture decisions
  - **Scope**: Limit to specific file types if needed

#### Code Review Agents (Bugbot)
- **Purpose**: PR code review and quality checks
- **Configuration**: `.cursor/BUGBOT.md`
- **Usage**: Automatically reviews PRs using project-specific guidelines
- **Features**: 
  - Database migration validation
  - Code quality checks
  - Security validation
  - Architecture compliance

#### Chat Agents
- **Purpose**: Interactive assistance during development
- **Configuration**: Rules in `.cursor/rules/` (auto-loaded based on context)
- **Behavior**: 
  - Rules load automatically when editing matching files
  - Uses glob patterns for efficient loading
  - References other rules to minimize token usage

### Role-Specific Behaviors

**Analyst**: 
- Prioritizes requirements analysis and technical planning
- Focuses on feasibility and architecture decisions
- Emphasizes documentation and planning
- Uses: `make setup`, `make analyze-full`, `make coverage`

**Developer**:
- Prioritizes implementation and code quality
- Runs quality checks (`make analyze-full`) automatically
- Focuses on tests and production-ready code
- Uses: `make format`, `make test`, `make analyze-full`

**Reviewer**:
- Prioritizes code review and quality validation
- Checks for compliance with project standards
- Validates tests and quality gates
- Uses: `gh pr checkout`, `make analyze-full`, `make test`

**Team Lead**:
- Manages PR workflows and team coordination
- Ensures quality gates are met
- Coordinates between team members
- Uses: `gh pr list`, `gh pr edit`, `gh pr review`

### Optimization Tips

For maximum efficiency with Cursor:

1. **Use Team Role**: Select your role in settings for role-specific behavior
2. **Rule Awareness**: Rules auto-load based on file patterns (glob patterns)
3. **Reference Rules**: Use `@rule-name.mdc` in prompts when needed
4. **Context First**: Search codebase before asking questions
5. **Quality Gates**: Always run `make analyze-full` before completion

See:
- [Cursor Optimization Guide](CURSOR_OPTIMIZATION.md) - General optimization
- [Cursor Agents Guide](CURSOR_AGENTS.md) - Detailed agent configuration

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
