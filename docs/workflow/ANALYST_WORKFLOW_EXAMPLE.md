# Analyst Workflow Example

Complete example of using the automated analyst workflow script.

## Scenario

**Analyst**: Needs to analyze adding a new payment calculation feature.

## Workflow

### 1. Start Script

```bash
./scripts/analyst-workflow.sh
```

### 2. Answer Questions

```
What are you analyzing? 
> add payment calculation

What type of change is this?
> 1 (feat)

What area does this affect?
> service
```

### 3. Automated Steps

Script automatically:
- Sets up project
- Creates branch: `feat/service-add-payment-calculation`
- Runs analysis: `make analyze-full`, `make coverage`
- Formats and checks code
- Commits with correct format
- Pushes branch
- Creates PR with auto-generated description

### 4. Result

PR created with:
- Auto-generated description from git diff
- All quality checks passed
- Ready for review

## What Gets Validated

- Branch name: `type/scope-description` format
- Commit message: Conventional Commits format
- Code formatting: Automatically formatted
- Quality checks: All quality gates verified
- PR description: Auto-generated from git diff

## Key Benefits

- Zero knowledge required - just answer questions
- Automatic validation - all checks done automatically
- Correct format - branch names and commits follow conventions
- Complete workflow - from hypothesis to PR in one command
