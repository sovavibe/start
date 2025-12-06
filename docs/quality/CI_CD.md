# CI/CD Pipeline

Automated checks and builds ensure code quality, security, and deployability. All jobs run in parallel where possible.

## Pipeline Architecture

```
Format Check (parallel)
    ↓
Code Quality (parallel)
    ↓
Tests + Coverage (parallel)
    ↓
Build (after all)
    ↓
Security (parallel: OWASP + Trivy)
    ↓
Docker Build (push only)
```

## Pipeline Jobs

### 1. Format Check
- **Tool**: Spotless
- **Command**: `./gradlew spotlessCheck`
- **Threshold**: Must pass (fail-fast)

### 2. Code Quality
- **Tools**: Checkstyle, PMD, SpotBugs, SonarLint, stylelint, Prettier
- **Command**: `./gradlew codeQualityFull`
- **Threshold**: All checks must pass (`ignoreFailures=false`)
- **Artifacts**: `build/reports/` (30 days retention)

### 3. Tests
- **Tools**: JUnit 5, Testcontainers, JaCoCo
- **Command**: `./gradlew test jacocoTestReport jacocoTestCoverageVerification`
- **Threshold**: All tests pass, Coverage: Instructions ≥75%, Branches ≥65%, Lines ≥75% (current coverage: 79%/68%/79%)
- **Requires**: Docker (for Testcontainers)
- **Note**: Thresholds align with BugBot guidelines (≥75% instructions, ≥65% branches, ≥75% lines). Target thresholds (85%/75%/90%) documented in `TODO.md` for future increase.

### 4. Build
- **Command**: `./gradlew -Pvaadin.productionMode=true bootJar`
- **Dependencies**: format-check, code-quality, test
- **Artifacts**: Application JAR (7 days retention)

### 5. Security
- **Tools**: OWASP Dependency-Check, Trivy
- **Commands**: 
  - `./gradlew dependencyCheckAnalyze`
  - `trivy fs --format sarif --output trivy-results.sarif .`
- **Artifacts**: OWASP report, Trivy SARIF (30 days retention)
- **Integration**: Trivy results uploaded to GitHub Security

### 6. Docker Build
- **Dependencies**: build, security
- **Condition**: Only on push to `main` or `develop`
- **Image**: `sovavibe/start`
- **Tags**: Branch name, SHA, `latest` (for default branch)
- **Scanning**: Trivy scans built image

## Quality Gate Thresholds

| Metric | Threshold | Tool |
|--------|-----------|------|
| Instructions | ≥75% | JaCoCo |
| Branches | ≥65% | JaCoCo |
| Lines | ≥75% | JaCoCo |
| Checkstyle | `maxWarnings=0` | Checkstyle |
| PMD | `ignoreFailures=false` | PMD |
| SpotBugs | `ignoreFailures=false` | SpotBugs |
| SonarLint | `ignoreFailures=false` | SonarLint |

## Branch Strategy

### Protected Branches
- **main**: Production-ready code
- **develop**: Integration branch

### Branch Protection Rules
- Require status checks to pass
- Require branches to be up to date
- Require pull request reviews
- Require quality gate to pass

### Workflow Triggers
```yaml
on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]
    types: [opened, synchronize, reopened, ready_for_review]
```

## Local CI Simulation

```bash
make ci              # Full CI pipeline
make format-check    # Format check
make analyze-full    # Code quality
make test            # Tests + coverage
make build           # Build
```

## Artifact Retention

| Artifact Type | Retention |
|---------------|-----------|
| Quality reports | 30 days |
| Test results | 30 days |
| Coverage reports | 30 days |
| Security reports | 30 days |
| Build artifacts | 7 days |

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Format check fails | Run `make format` locally |
| Quality check fails | Run `make analyze-full` locally, fix issues |
| Test failures | Run `make test` locally, check Testcontainers |
| Coverage below threshold | Check report, add tests |
| SonarLint issues | Check reports in `build/reports/sonarlint/` |

## References

- [Quality Gates Documentation](../quality/QUALITY_GATES.md)
- [SDLC Documentation](../workflow/SDLC.md)
- [Local Development Guide](../getting-started/SETUP.md)
