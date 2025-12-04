# Analyst Workflow Example

This document shows a complete example of using the automated analyst workflow script.

## Scenario

**Analyst**: Needs to analyze adding a new payment calculation feature to the service layer.

## Step-by-Step Example

### 1. Start the Script

```bash
cd /path/to/start
./scripts/analyst-workflow.sh
```

### 2. Script Output and Interactions

```
╔════════════════════════════════════════════════════════════╗
║     Analyst Workflow - Automated from Start to PR         ║
╚════════════════════════════════════════════════════════════╝

Step 1: Setting up project...
✅ Setup complete

Step 2: What are you analyzing?
Enter your hypothesis/idea (what needs to be done): add payment calculation
```

**Analyst enters**: `add payment calculation`

```
Step 3: What type of change is this?
1) feat - New feature
2) fix - Bug fix
3) docs - Documentation
4) refactor - Code refactoring
5) test - Tests
6) chore - Maintenance
Choose (1-6): 1
```

**Analyst enters**: `1` (for feat)

```
Step 4: What area does this affect?
Common scopes: jmix, vaadin, service, entity, view, config, ci, test
Enter scope (or press Enter for 'config'): service
```

**Analyst enters**: `service`

```
Step 5: Generating branch name...
Branch name: feat/service-add-payment-calculation
✅ Branch created

Step 6: Creating branch...
✅ Branch created

Step 7: Running analysis...
Checking code quality baseline...
[Gradle output...]
✅ Quality check completed

Checking test coverage...
[Coverage report...]
✅ Coverage check completed

╔════════════════════════════════════════════════════════════╗
║                    Analysis Complete!                      ║
╚════════════════════════════════════════════════════════════╝

Summary:
  Hypothesis: add payment calculation
  Type: feat
  Scope: service
  Branch: feat/service-add-payment-calculation

Step 8: Did you make any code/documentation changes?
Have changes been made? (y/n): y
```

**Analyst enters**: `y` (if they made changes)

```
Step 9: Formatting and checking code...
[Formatting output...]
✅ Code formatted

[Quality check output...]
✅ Quality check completed

Step 10: Committing changes...
[Git add output...]
✅ Changes committed

Step 11: Pushing branch...
[Git push output...]
✅ Branch pushed

Step 12: Creating Pull Request...
📝 Analyzing changes and generating PR description...
🚀 Creating PR...
Title: feat(service): add payment calculation
Base: main
Head: feat/service-add-payment-calculation

📊 Changes: 3 files changed, 45 insertions(+), 12 deletions(-)
📝 Commits: 1

✅ PR created successfully!
🔍 SpotBugs review will be added automatically when PR is opened
```

### 3. Final Result

```
╔════════════════════════════════════════════════════════════╗
║                      All Done! 🎉                          ║
╚════════════════════════════════════════════════════════════╝

Next steps:
  • PR will be automatically reviewed
  • SpotBugs review will be added automatically
  • Wait for CI/CD checks to pass
  • Team Lead will assign reviewer
```

## What Happened Automatically

1. ✅ Project setup verified/completed
2. ✅ Branch created: `feat/service-add-payment-calculation`
3. ✅ Code quality analysis run: `make analyze-full`
4. ✅ Test coverage checked: `make coverage`
5. ✅ Code formatted: `make format`
6. ✅ Quality checks run: `make analyze-full`
7. ✅ Changes committed: `feat(service): add payment calculation`
8. ✅ Branch pushed to remote
9. ✅ PR created with auto-generated description

## PR Description (Auto-Generated)

The PR will have a detailed description including:

```markdown
## Description

### Summary

- feat(service): add payment calculation

## Type

- [x] `feat`

## Changes Overview

**Statistics:** 3 files changed, 45 insertions(+), 12 deletions(-)
**Commits:** 1
**Files changed:**
- Java: 2
- Tests: 1
- Config: 0
- Docs: 0
- Other: 0

### Java Files (2)

✏️ `src/main/java/com/digtp/start/service/PaymentService.java`
➕ `src/main/java/com/digtp/start/service/PaymentCalculator.java`

### Test Files (1)

✅ `src/test/java/com/digtp/start/service/PaymentServiceTest.java`

## Related

<!-- Link issues: Closes #123, Related to #456 -->

## Testing

- [x] Tests added/updated (1 files)
- [ ] All existing tests pass
- [ ] Tested locally
- [ ] Tested in CI/CD

## Quality

- [x] `make format` passed
- [x] `make analyze-full` passed
- [ ] No new suppressions added (or documented if needed)
- [x] Code follows project conventions
```

## Example 2: Documentation Update (No Code Changes)

```
Step 2: What are you analyzing?
> update setup documentation

Step 3: What type of change is this?
> 3

Step 4: What area does this affect?
> docs

Step 5: Generating branch name...
Branch name: docs/docs-update-setup-documentation

Step 8: Did you make any code/documentation changes?
> y

[Script formats, checks, commits, pushes, creates PR]
```

## Example 3: Analysis Only (No Changes)

```
Step 2: What are you analyzing?
> analyze performance issues

Step 3: What type of change is this?
> 1

Step 4: What area does this affect?
> service

Step 8: Did you make any code/documentation changes?
> n

[Script completes analysis, shows summary, stops]
```

## What Gets Validated Automatically

1. **Branch Name**: Must follow `type/scope-description` format
2. **Commit Message**: Must follow Conventional Commits format
3. **Code Formatting**: Automatically formatted
4. **Quality Checks**: All quality gates verified
5. **PR Description**: Auto-generated from git diff

## Troubleshooting

### If setup fails:
```bash
# Run setup manually
make setup
# Then run script again
./scripts/analyst-workflow.sh
```

### If push fails:
```bash
# Push manually
git push -u origin feat/service-add-payment-calculation
# Then create PR manually
./scripts/create-pr.sh
```

### If PR creation fails:
```bash
# Create PR manually
gh pr create --title "feat(service): add payment calculation"
```

## Key Benefits

✅ **Zero knowledge required** - Just answer questions
✅ **Automatic validation** - All checks done automatically
✅ **Correct format** - Branch names and commits follow conventions
✅ **Complete workflow** - From hypothesis to PR in one command
✅ **Error handling** - Script guides you if something fails

