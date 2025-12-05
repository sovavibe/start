# Quality Gates Documentation

This document provides comprehensive documentation for all quality gates used in the Start project, following Vibe Coding principles.

## Overview

Quality gates ensure code quality, security, and maintainability. All gates are **strict** (`ignoreFailures=false`) and must pass before code can be merged.

## Quality Gate Philosophy

### Vibe Coding Principles

- **Minimal Suppressions**: Fix root causes, not symptoms
- **Strict Enforcement**: All checks must pass
- **Framework-Specific Only**: Suppress only for Jmix/Vaadin/Lombok false positives
- **Production-Ready**: Code must meet production standards from day one

### Quality Gate Thresholds

| Metric | Threshold | Tool |
|--------|-----------|------|
| **Code Coverage (Instructions)** | ≥85% | JaCoCo |
| **Code Coverage (Branches)** | ≥75% | JaCoCo |
| **Code Coverage (Lines)** | ≥90% | JaCoCo |
| **Cognitive Complexity** | ≤10 | SonarLint, PMD |
| **Cyclomatic Complexity** | ≤10 | PMD, SonarLint |
| **File Length** | ≤250 lines | Checkstyle, SonarLint |
| **Code Duplication** | <3% | SonarLint |
| **Warnings (Checkstyle)** | 0 | Checkstyle |
| **Failures (All Tools)** | 0 | All tools |

## Static Analysis Tools

### Checkstyle

**Purpose**: Code style and formatting compliance

**Configuration**: 
- Managed by [Palantir Baseline](https://github.com/palantir/gradle-baseline)
- Custom suppressions: `.baseline/checkstyle/custom-suppressions.xml`

**Thresholds**:
- `ignoreFailures = false`
- `maxWarnings = 0`

**Documentation**: [Checkstyle](https://checkstyle.sourceforge.io/)

**Usage**:
```bash
# Run Checkstyle
./gradlew checkstyleMain checkstyleTest

# Or via quality task
make analyze-full
```

**Suppression Policy**:
- Use `@SuppressWarnings("checkstyle:RuleName")` for framework-specific cases
- Document why suppression is needed
- Prefer fixing code over suppressing

**Example Suppression**:
```java
// Framework pattern: Jmix views don't need serialVersionUID (framework-managed)
@SuppressWarnings("checkstyle:MissingSerialVersionUID")
public class UserListView extends StandardListView<User> {
    // ...
}
```

### PMD

**Purpose**: Static code analysis for bug patterns and code quality

**Configuration**:
- Tool version: 7.9.0
- Managed by Palantir Baseline
- Framework-specific suppressions via `@SuppressWarnings("PMD.RuleName")`

**Thresholds**:
- `ignoreFailures = false`

**Documentation**: [PMD](https://pmd.github.io/)

**Usage**:
```bash
# Run PMD
./gradlew pmdMain pmdTest

# Or via quality task
make analyze-full
```

**Common Rules**:
- `PMD.MissingSerialVersionUID`: Suppressed for Jmix views (framework-managed)
- `PMD.NonSerializableClass`: Suppressed for views with framework dependencies
- `PMD.CommentSize`: Handled by Baseline defaults

**Suppression Policy**:
- Framework-specific false positives only
- Document with comment explaining why

### SpotBugs

**Purpose**: Bug pattern detection using static analysis

**Configuration**:
- Tool version: 6.4.7
- Exclude filter: `config/spotbugs/exclude.xml`
- Effort: `MAX`
- Plugins: FindSecBugs (security patterns)

**Thresholds**:
- `ignoreFailures = false`
- `effort = Effort.MAX`

**Documentation**: [SpotBugs](https://spotbugs.github.io/)

**Usage**:
```bash
# Run SpotBugs
./gradlew spotbugsMain spotbugsTest

# Or via quality task
make analyze-full
```

**Exclusions**:
- EclipseLink generated methods (bytecode-generated, cannot be matched)
- Framework-specific patterns documented in `config/spotbugs/exclude.xml`

**Security Plugin**:
- FindSecBugs plugin enabled for security vulnerability detection
- Detects SQL injection, XSS, insecure random, etc.

### SonarLint

**Purpose**: Code quality and security analysis (works locally and in CI/CD)

**Configuration**:
- Centralized: `sonar-project.properties` (project root, single source of truth)
- Rules automatically extracted from multicriteria entries
- Languages: Java, XML

**Thresholds**:
- `ignoreFailures = false`

**Documentation**: 
- [SonarLint Gradle Plugin](SONARLINT_GRADLE_PLUGIN.md)
- [SonarLint](https://www.sonarsource.com/products/sonarlint/)

**Usage**:
```bash
# Run SonarLint
./gradlew sonarlintMain sonarlintTest

# Or via quality task
make analyze-full
```

**Configuration Details**:
- All settings from `sonar-project.properties` (project root)
- Rules automatically extracted from multicriteria (no manual sync needed)
- Framework-specific exclusions via multicriteria
- Works without server connection (standalone mode)

**Excluded Rules**: See `sonar-project.properties` for complete list of framework-specific exclusions (automatically applied)

### Error-prone

**Purpose**: Compile-time error detection and prevention

**Configuration**:
- Version: 2.36.0
- NullAway enabled for null safety
- UnusedMethod detection enabled

**Thresholds**:
- Compilation fails on errors

**Documentation**: [Error Prone](https://errorprone.info/)

**Usage**:
```bash
# Runs automatically during compilation
./gradlew compileJava
```

**Key Checks**:
- **NullAway**: Null safety analysis
  - Annotated packages: `com.digtp.start`
  - Excluded: Jmix entities, test classes, framework annotations
- **UnusedMethod**: Dead code detection
  - Excluded annotations: Only method-level annotations (Spring `@Bean`, `@EventListener`, `@Scheduled`, Jmix `@Install`, `@Subscribe`)
  - Class-level annotations (`@Service`, `@Component`, `@Repository`, `@Controller`, `@Configuration`) do NOT exclude methods
  - Rationale: Class-level annotations mark the class as a Spring bean but don't indicate that all methods are framework callbacks
- **TypeParameterUnusedInFormals**: Suppressed for test reflection utilities

**NullAway Configuration**:
```groovy
option("NullAway:AnnotatedPackages", "com.digtp.start")
option("NullAway:ExcludedClassAnnotations", "io.jmix.core.metamodel.annotation.JmixEntity")
option("NullAway:ExcludedFieldAnnotations", "io.jmix.flowui.view.ViewComponent|...")
option("NullAway:ExcludedMethodAnnotations", "io.jmix.flowui.view.Install")
option("NullAway:ExcludedClasses", ".*Test")
```

## Code Coverage

### JaCoCo

**Purpose**: Code coverage measurement and verification

**Configuration**:
- Tool version: 0.8.13
- Report formats: XML, HTML
- Coverage thresholds enforced

**Thresholds**:
- **Instructions**: ≥85%
- **Branches**: ≥75%
- **Lines**: ≥90%

**Documentation**: [JaCoCo](https://www.jacoco.org/jacoco/)

**Usage**:
```bash
# Generate coverage report
./gradlew jacocoTestReport

# Verify coverage thresholds
./gradlew jacocoTestCoverageVerification

# Or via make
make coverage
```

**Coverage Verification**:
```groovy
violationRules {
    rule {
        limit {
            counter = 'INSTRUCTION'
            minimum = 0.85
        }
        limit {
            counter = 'BRANCH'
            minimum = 0.75
        }
        limit {
            counter = 'LINE'
            minimum = 0.90
        }
    }
}
```

**Reports**:
- HTML: `build/reports/jacoco/test/html/index.html`
- XML: `build/reports/jacoco/test/jacocoTestReport.xml`

## Code Formatting

### Spotless

**Purpose**: Code formatting and import organization

**Configuration**:
- Java: Palantir Java Format 2.68.0
- Import order: Palantir Baseline defaults
- CleanThat: Source compatibility 21

**Documentation**: [Spotless](https://github.com/diffplug/spotless)

**Usage**:
```bash
# Check formatting
./gradlew spotlessCheck

# Format code
./gradlew spotlessApply

# Or via make
make format-check
make format
```

**Features**:
- Automatic import ordering
- Unused import removal
- Trailing whitespace removal
- End-of-file newline
- Format annotations

**Integration**:
- Runs before compilation
- Fails build if formatting violations found
- Can be auto-fixed with `spotlessApply`

- Requires coverage report: `build/reports/jacoco/test/jacocoTestReport.xml`
- Integrates with GitHub PRs

**Metrics Tracked**:
- Code coverage
- Cognitive complexity
- Cyclomatic complexity
- Code duplication
- Security vulnerabilities
- Code smells
- Maintainability rating

**Configuration**:
- Centralized in `sonar-project.properties` (project root)
- Framework-specific exclusions via multicriteria
- Complexity threshold: 10

## Frontend Quality

### stylelint (CSS)

**Purpose**: CSS code quality and style checking

**Configuration**:
- Standard configuration
- Targets: `src/main/frontend/themes/**/*.css`

**Documentation**: [stylelint](https://stylelint.io/)

**Usage**:
```bash
# Check CSS
./gradlew lintCss

# Fix CSS
./gradlew lintCssFix

# Or via make
make format-check
make format
```

### Prettier (YAML)

**Purpose**: YAML file formatting

**Configuration**:
- Targets: All `.yml` and `.yaml` files (excluding node_modules, build, .jmix, helm)

**Documentation**: [Prettier](https://prettier.io/)

**Usage**:
```bash
# Check YAML
./gradlew lintYaml

# Fix YAML
./gradlew lintYamlFix

# Or via make
make format-check
make format
```

## Security Scanning

### OWASP Dependency-Check

**Purpose**: Dependency vulnerability scanning

**Configuration**:
- Tool version: 12.1.9
- Scans all dependencies for known vulnerabilities

**Documentation**: [OWASP Dependency-Check](https://owasp.org/www-project-dependency-check/)

**Usage**:
```bash
# Run OWASP scan
./gradlew dependencyCheckAnalyze

# View report
open build/reports/dependency-check-report.html
```

**Reports**:
- HTML: `build/reports/dependency-check-report.html`
- Uploaded as artifact in CI/CD

### Trivy

**Purpose**: Container and filesystem vulnerability scanning

**Configuration**:
- Scan type: Filesystem and container images
- Format: SARIF (for GitHub Security)

**Documentation**: [Trivy](https://aquasecurity.github.io/trivy/)

**Usage**:
```bash
# Run Trivy (CI/CD)
# Filesystem scan
trivy fs --format sarif --output trivy-results.sarif .

# Container scan
trivy image --format sarif --output trivy-image-results.sarif image:tag
```

**Integration**:
- Runs in CI/CD pipeline
- Results uploaded to GitHub Security
- SARIF format for GitHub integration

## Mutation Testing

### PIT (PITest)

**Purpose**: Mutation testing to verify test quality

**Configuration**:
- Mutation threshold: 70%
- Coverage threshold: 80%
- Target classes: `com.digtp.start.*`
- Excluded: Entities, config, application class

**Documentation**: [PITest](https://pitest.org/)

**Usage**:
```bash
# Run mutation testing
./gradlew pitest

# Or via make
make mutation
```

**Thresholds**:
- Mutation threshold: ≥70%
- Coverage threshold: ≥80%

**Excluded Classes**:
- Entities (data classes)
- Configuration classes
- Application class

## Quality Gate Integration

### Local Development

```bash
# Run all quality checks
make analyze-full

# This runs:
# - Checkstyle
# - PMD
# - SpotBugs
# - SonarLint
# - CSS linting
# - YAML linting
```

### CI/CD Pipeline

Quality gates are enforced in `.github/workflows/ci.yml`:

1. **Format Check**: `spotlessCheck`
2. **Code Quality**: `codeQualityFull` (includes SonarLint)
3. **Tests**: `test` + coverage verification
4. **Security**: OWASP + Trivy

All jobs must pass before merge.

## Suppression Policy

### When to Suppress

Suppress warnings only for:
1. **Framework-specific false positives** (Jmix/Vaadin/Lombok)
2. **Generated code** (Lombok, EclipseLink)
3. **Test patterns** (reflection, multiple assertions)

### How to Suppress

1. **Use specific rule**: `@SuppressWarnings("checkstyle:RuleName")`
2. **Document why**: Add comment explaining suppression
3. **Local first**: Prefer inline over global configs
4. **Framework-only**: Only for framework patterns

### Suppression Examples

**Good** (framework-specific):
```java
// Framework pattern: Jmix views don't need serialVersionUID (framework-managed)
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class UserListView extends StandardListView<User> {
    // ...
}
```

**Bad** (should fix code):
```java
// Don't suppress - fix the actual issue
@SuppressWarnings("PMD.UnusedPrivateField")
private String unusedField; // Remove this field instead
```

## Quality Metrics Dashboard

### Local Reports

- **SonarLint**: `build/reports/sonarlint/`
- **Checkstyle**: `build/reports/checkstyle/`
- **PMD**: `build/reports/pmd/`
- **SpotBugs**: `build/reports/spotbugs/`
- **JaCoCo**: `build/reports/jacoco/test/html/`
- **Quality Gate**: Pass/Fail status
- **Trends**: Historical data

### Local Reports

- **Checkstyle**: `build/reports/checkstyle/`
- **PMD**: `build/reports/pmd/`
- **SpotBugs**: `build/reports/spotbugs/`
- **SonarLint**: `build/reports/sonarlint/`
- **JaCoCo**: `build/reports/jacoco/test/html/index.html`

## Best Practices

### During Development

1. **Run quality checks frequently**
   ```bash
   make analyze-full
   ```

2. **Fix issues immediately**
   - Don't accumulate technical debt
   - Address quality gate failures
   - Fix root causes

3. **Maintain coverage**
   - Write tests alongside code
   - Test edge cases
   - Verify coverage thresholds

### Before Committing

1. **Format code**
   ```bash
   make format
   ```

2. **Run quality checks**
   ```bash
   make analyze-full
   ```

3. **Run tests**
   ```bash
   make test
   ```

### Before PR

1. **All quality gates pass**
2. **Coverage thresholds met**
3. **No new suppressions** (or documented if needed)
4. **Documentation updated**

## Troubleshooting

### Quality Gate Failures

1. **Check local reports**: `build/reports/`
2. **Run locally**: `make analyze-full`
3. **Fix issues**: Address root causes
4. **Re-run**: Verify fixes

### Coverage Issues

1. **Check coverage report**: `build/reports/jacoco/test/html/index.html`
2. **Identify uncovered code**: Review report
3. **Add tests**: Cover missing code
4. **Re-run**: Verify coverage

### Suppression Questions

1. **Is it framework-specific?** If no, fix code
2. **Is it documented?** Must explain why
3. **Is it necessary?** Avoid if possible

## References

- [Palantir Baseline](https://github.com/palantir/gradle-baseline)
- [Checkstyle](https://checkstyle.sourceforge.io/)
- [PMD](https://pmd.github.io/)
- [SpotBugs](https://spotbugs.github.io/)
- [SonarLint](https://www.sonarsource.com/products/sonarlint/)
- [Error Prone](https://errorprone.info/)
- [JaCoCo](https://www.jacoco.org/jacoco/)
- [Spotless](https://github.com/diffplug/spotless)
- [OWASP Dependency-Check](https://owasp.org/www-project-dependency-check/)
- [Trivy](https://aquasecurity.github.io/trivy/)
- [PITest](https://pitest.org/)

## Related Documentation

- [SDLC Documentation](../development/SDLC.md) - Quality gates in development lifecycle
- [CI/CD Documentation](../ci-cd/CI_CD.md) - Quality gates in CI/CD pipeline
- [Contributing Guide](../../CONTRIBUTING.md) - Quality requirements for contributors

