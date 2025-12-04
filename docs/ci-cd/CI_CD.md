# CI/CD Pipeline Documentation

This document describes the Continuous Integration and Continuous Deployment (CI/CD) pipeline for the Start project.

## Overview

The CI/CD pipeline ensures code quality, security, and deployability through automated checks and builds. All jobs run in parallel where possible for efficiency, with strict fail-fast behavior.

## Pipeline Architecture

```
┌─────────────────┐
│   Format Check  │ (parallel)
└─────────────────┘
         │
┌─────────────────┐
│  Code Quality   │ (parallel)
└─────────────────┘
         │
┌─────────────────┐
│     Tests       │ (parallel)
│  + Coverage     │
│  + SonarCloud   │
└─────────────────┘
         │
┌─────────────────┐
│     Build       │ (after all)
└─────────────────┘
         │
┌─────────────────┐
│    Security     │ (parallel)
│  OWASP + Trivy  │
└─────────────────┘
         │
┌─────────────────┐
│  Docker Build   │ (push only)
└─────────────────┘
```

## Pipeline Jobs

### 1. Format Check

**Job Name**: `format-check`

**Purpose**: Verify code formatting compliance

**Configuration**:
- Runs on: `ubuntu-latest`
- Timeout: 10 minutes
- Parallel execution: Yes

**Steps**:
1. Checkout code
2. Set up JDK 21
3. Make gradlew executable
4. Run `./gradlew spotlessCheck`

**Quality Gate**:
- Must pass (fail-fast)
- Blocks merge on failure

**Tool**: [Spotless](https://github.com/diffplug/spotless)

**Command**:
```bash
./gradlew spotlessCheck --no-daemon
```

### 2. Code Quality

**Job Name**: `code-quality`

**Purpose**: Run all static analysis tools

**Configuration**:
- Runs on: `ubuntu-latest`
- Timeout: 20 minutes
- Parallel execution: Yes

**Steps**:
1. Checkout code
2. Set up JDK 21
3. Make gradlew executable
4. Run `./gradlew codeQualityFull --no-daemon -x test`
5. Upload quality reports

**Quality Gates**:
- Checkstyle: `maxWarnings=0`, `ignoreFailures=false`
- PMD: `ignoreFailures=false`
- SpotBugs: `ignoreFailures=false`
- SonarLint: `ignoreFailures=false`
- CSS linting: stylelint
- YAML linting: prettier

**Tools**:
- [Checkstyle](https://checkstyle.sourceforge.io/)
- [PMD](https://pmd.github.io/)
- [SpotBugs](https://spotbugs.github.io/)
- [SonarLint](https://www.sonarsource.com/products/sonarlint/)
- [stylelint](https://stylelint.io/)
- [Prettier](https://prettier.io/)

**Command**:
```bash
./gradlew codeQualityFull --no-daemon -x test
```

**Artifacts**:
- Quality reports: `build/reports/`
- Retention: 30 days

### 3. Tests

**Job Name**: `test`

**Purpose**: Run tests, generate coverage, and analyze with SonarCloud

**Configuration**:
- Runs on: `ubuntu-latest`
- Timeout: 30 minutes
- Parallel execution: Yes
- Requires: Docker (for Testcontainers)

**Steps**:
1. Checkout code
2. Set up JDK 21
3. Make gradlew executable
4. Run tests: `./gradlew test --no-daemon`
5. Generate coverage: `./gradlew jacocoTestReport --no-daemon`
6. Verify coverage: `./gradlew jacocoTestCoverageVerification --no-daemon`
7. Upload test results
8. Upload coverage report
9. Verify coverage report exists
10. SonarCloud scan: `./gradlew sonar --no-daemon`

**Quality Gates**:
- All tests must pass
- Coverage thresholds:
  - Instructions: ≥85%
  - Branches: ≥75%
  - Lines: ≥90%
- SonarCloud quality gate must pass

**Tools**:
- [JUnit 5](https://junit.org/junit5/)
- [Testcontainers](https://www.testcontainers.org/)
- [JaCoCo](https://www.jacoco.org/jacoco/)
- [SonarCloud](https://docs.sonarcloud.io/)

**Environment Variables**:
```yaml
TESTCONTAINERS_REUSE_ENABLE: false
GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
```

**Commands**:
```bash
# Run tests
./gradlew test --no-daemon

# Generate coverage
./gradlew jacocoTestReport --no-daemon

# Verify coverage
./gradlew jacocoTestCoverageVerification --no-daemon

# SonarCloud scan
./gradlew sonar --no-daemon
```

**Artifacts**:
- Test results: `build/reports/tests/test/`
- Coverage report: `build/reports/jacoco/test/html/`
- Retention: 30 days

**SonarCloud Integration**:
- Quality gate blocks merge on failure
- Requires coverage report: `build/reports/jacoco/test/jacocoTestReport.xml`
- Project: `sovavibe_start`
- Organization: `sovavibe`

### 4. Build

**Job Name**: `build`

**Purpose**: Build application artifacts

**Configuration**:
- Runs on: `ubuntu-latest`
- Timeout: 15 minutes
- Dependencies: `format-check`, `code-quality`, `test`
- Condition: All dependencies must pass

**Steps**:
1. Checkout code
2. Set up JDK 21
3. Make gradlew executable
4. Build: `./gradlew -Pvaadin.productionMode=true bootJar --no-daemon -x test`
5. Upload build artifact

**Command**:
```bash
./gradlew -Pvaadin.productionMode=true bootJar --no-daemon -x test
```

**Artifacts**:
- Application JAR: `build/libs/*.jar`
- Retention: 7 days

### 5. Security

**Job Name**: `security`

**Purpose**: Security vulnerability scanning

**Configuration**:
- Runs on: `ubuntu-latest`
- Timeout: 30 minutes
- Parallel execution: Yes

**Steps**:
1. Checkout code
2. Set up JDK 21
3. Make gradlew executable
4. OWASP Dependency Check: `./gradlew dependencyCheckAnalyze --no-daemon`
5. Trivy filesystem scan
6. Upload OWASP report
7. Upload Trivy results to GitHub Security

**Tools**:
- [OWASP Dependency-Check](https://owasp.org/www-project-dependency-check/)
- [Trivy](https://aquasecurity.github.io/trivy/)

**Commands**:
```bash
# OWASP scan
./gradlew dependencyCheckAnalyze --no-daemon

# Trivy scan (CI/CD)
trivy fs --format sarif --output trivy-results.sarif .
```

**Artifacts**:
- OWASP report: `build/reports/dependency-check-report.html`
- Trivy SARIF: `trivy-results.sarif`
- Retention: 30 days

**GitHub Security Integration**:
- Trivy results uploaded to GitHub Security
- Visible in Security tab
- SARIF format for automated analysis

### 6. Docker Build

**Job Name**: `docker-build`

**Purpose**: Build and push Docker images

**Configuration**:
- Runs on: `ubuntu-latest`
- Timeout: 20 minutes
- Dependencies: `build`, `security`
- Condition: Only on push to `main` or `develop` branches

**Steps**:
1. Checkout code
2. Set up Docker Buildx
3. Log in to Docker Hub
4. Extract metadata (tags, labels)
5. Build and push Docker image
6. Run Trivy on Docker image
7. Upload Trivy image results to GitHub Security

**Docker Configuration**:
- Image: `sovavibe/start`
- Tags: Branch name, SHA, `latest` (for default branch)
- Cache: GitHub Actions cache backend

**Tools**:
- [Docker](https://docs.docker.com/)
- [Docker Buildx](https://docs.docker.com/buildx/)
- [Trivy](https://aquasecurity.github.io/trivy/)

**Image Scanning**:
- Trivy scans built Docker image
- Results uploaded to GitHub Security
- SARIF format

## Quality Gate Thresholds

### Code Coverage

| Metric | Threshold | Tool |
|--------|-----------|------|
| Instructions | ≥85% | JaCoCo |
| Branches | ≥75% | JaCoCo |
| Lines | ≥90% | JaCoCo |

### Static Analysis

| Tool | Threshold |
|------|-----------|
| Checkstyle | `maxWarnings=0`, `ignoreFailures=false` |
| PMD | `ignoreFailures=false` |
| SpotBugs | `ignoreFailures=false` |
| SonarLint | `ignoreFailures=false` |

### SonarCloud Quality Gate

- Must pass (blocks merge on failure)
- Coverage thresholds enforced
- Complexity: ≤10
- Duplication: <3%

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

## Release Workflow

### Creating a Release

1. Create release tag:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

2. Release workflow triggers:
   - Docker image build with version tag
   - GitHub Release creation
   - Artifact uploads

### Release Artifacts

- Docker image: `sovavibe/start:v1.0.0`
- Application JAR
- Release notes

## Artifact Management

### Artifact Retention

| Artifact Type | Retention |
|---------------|-----------|
| Quality reports | 30 days |
| Test results | 30 days |
| Coverage reports | 30 days |
| Security reports | 30 days |
| Build artifacts | 7 days |

### Artifact Access

- Available in GitHub Actions workflow runs
- Downloadable from workflow summary
- Automatic cleanup after retention period

## Parallelization

### Parallel Jobs

- `format-check`: Independent
- `code-quality`: Independent
- `test`: Independent
- `security`: Independent

### Sequential Jobs

- `build`: Depends on `format-check`, `code-quality`, `test`
- `docker-build`: Depends on `build`, `security`

### Concurrency Control

```yaml
concurrency:
  group: ${{ github.workflow }}-${{ github.ref }}
  cancel-in-progress: true
```

This ensures:
- Only latest commit runs for each branch
- Previous runs are cancelled (fail-fast)
- Resource efficiency

## Fail-Fast Behavior

### Job Dependencies

Jobs are configured to fail fast:
- Dependent jobs don't run if dependencies fail
- Parallel jobs can fail independently
- Overall workflow fails if any required job fails

### Quality Gate Enforcement

- Formatting violations block merge
- Quality check failures block merge
- Test failures block merge
- Coverage threshold violations block merge
- SonarCloud quality gate failures block merge

## Local CI Simulation

### Running Full CI Pipeline Locally

```bash
# Full CI pipeline
make ci

# Individual checks
make format-check    # Format check
make analyze-full    # Code quality
make test            # Tests + coverage
make build           # Build
```

### CI Commands

```bash
# Format check
./gradlew spotlessCheck --no-daemon

# Code quality
./gradlew codeQualityFull --no-daemon -x test

# Tests
./gradlew test --no-daemon

# Coverage
./gradlew jacocoTestReport --no-daemon
./gradlew jacocoTestCoverageVerification --no-daemon

# SonarCloud (requires token)
./gradlew sonar --no-daemon

# Build
./gradlew -Pvaadin.productionMode=true bootJar --no-daemon -x test

# Security
./gradlew dependencyCheckAnalyze --no-daemon
```

## Monitoring and Notifications

### Workflow Status

- Visible in GitHub repository
- Status badges in README
- PR status checks

### SonarCloud Integration

- Quality gate status in PR
- Coverage reports in PR
- Security vulnerabilities in PR

### GitHub Security

- Dependency vulnerabilities
- Code scanning alerts
- Security advisories

## Troubleshooting

### Common Issues

1. **Format Check Fails**
   - Run `make format` locally
   - Commit formatted code

2. **Quality Check Fails**
   - Run `make analyze-full` locally
   - Fix reported issues
   - Re-run checks

3. **Test Failures**
   - Run `make test` locally
   - Check Testcontainers setup
   - Verify test environment

4. **Coverage Threshold Violations**
   - Check coverage report
   - Add missing tests
   - Verify thresholds

5. **SonarCloud Quality Gate Fails**
   - Check SonarCloud dashboard
   - Review quality gate conditions
   - Address reported issues

### Debugging

1. **Check Workflow Logs**
   - View detailed logs in GitHub Actions
   - Identify failing step
   - Review error messages

2. **Reproduce Locally**
   - Run same commands locally
   - Verify environment matches
   - Check dependencies

3. **Artifact Inspection**
   - Download quality reports
   - Review test results
   - Check coverage reports

## Best Practices

### Before Pushing

1. Run local checks:
   ```bash
   make ci
   ```

2. Verify all checks pass

3. Ensure coverage thresholds met

### PR Best Practices

1. Keep PRs focused and small

2. Ensure all CI checks pass

3. Address review comments

4. Update documentation if needed

### Release Best Practices

1. Test release process

2. Verify all artifacts

3. Update changelog

4. Tag releases properly

## References

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [SonarCloud Documentation](https://docs.sonarcloud.io/)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/)
- [OWASP Dependency-Check](https://owasp.org/www-project-dependency-check/)
- [Trivy Documentation](https://aquasecurity.github.io/trivy/)
- [Docker Documentation](https://docs.docker.com/)

## Related Documentation

- [CI/CD Setup Guide](CI_CD_SETUP.md) - How to configure CI/CD secrets and services
- [Quality Gates Documentation](QUALITY_GATES.md) - Detailed quality gate configuration
- [SDLC Documentation](SDLC.md) - Quality gates in development lifecycle
- [Local Development Guide](LOCAL_DEVELOPMENT.md) - Local setup and testing

