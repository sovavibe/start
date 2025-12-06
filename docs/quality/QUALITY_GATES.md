# Quality Gates

All quality gates are strict (`ignoreFailures=false`) and must pass before merge.

## Principles

- **Minimal Suppressions**: Fix root causes, not symptoms
- **Framework-Specific Only**: Suppress only for Jmix/Vaadin/Lombok false positives
- **Production-Ready**: Code must meet production standards from day one

## Thresholds

| Metric | Threshold | Tool |
|--------|-----------|------|
| Code Coverage (Instructions) | ≥75% | JaCoCo |
| Code Coverage (Branches) | ≥65% | JaCoCo |
| Code Coverage (Lines) | ≥75% | JaCoCo |
| Cognitive Complexity | ≤10 | SonarLint, PMD |
| Cyclomatic Complexity | ≤10 | PMD, SonarLint |
| File Length | ≤250 lines | Checkstyle, SonarLint |
| Code Duplication | <3% | SonarLint |
| Warnings (All Tools) | 0 | All tools |

## Tools

### Checkstyle
- **Purpose**: Code style compliance
- **Config**: Palantir Baseline, `.baseline/checkstyle/custom-suppressions.xml`
- **Threshold**: `maxWarnings=0`, `ignoreFailures=false`
- **Usage**: `make analyze-full`

### PMD
- **Purpose**: Static code analysis
- **Config**: Palantir Baseline, version 7.9.0
- **Threshold**: `ignoreFailures=false`
- **Usage**: `make analyze-full`

### SpotBugs
- **Purpose**: Bug pattern detection
- **Config**: `config/spotbugs/exclude.xml`, effort: MAX, FindSecBugs enabled
- **Threshold**: `ignoreFailures=false`
- **Usage**: `make analyze-full`

### SonarLint
- **Purpose**: Code quality and security analysis
- **Config**: `sonar-project.properties` (project root)
- **Threshold**: `ignoreFailures=false`
- **Usage**: `make analyze-full`
- **Docs**: See `sonar-project.properties` for configuration

### Error-prone
- **Purpose**: Compile-time error detection
- **Config**: NullAway enabled, UnusedMethod detection
- **Threshold**: Compilation fails on errors
- **Usage**: Runs automatically during compilation

### JaCoCo
- **Purpose**: Code coverage measurement
- **Threshold**: Instructions ≥75%, Branches ≥65%, Lines ≥75% (current coverage: 79%/68%/79%)
- **Usage**: `make coverage`
- **Reports**: `build/reports/jacoco/test/html/index.html`
- **Note**: Thresholds align with BugBot guidelines (≥75% instructions, ≥65% branches, ≥75% lines). Target thresholds (85%/75%/90%) documented in `TODO.md` for future increase.

### Spotless
- **Purpose**: Code formatting
- **Config**: Palantir Java Format 2.68.0
- **Usage**: `make format` / `make format-check`

### Frontend Tools
- **stylelint**: CSS quality (`./gradlew lintCss`)
- **Prettier**: YAML formatting (`./gradlew lintYaml`)

### Security Scanning
- **OWASP Dependency-Check**: Dependency vulnerabilities (`./gradlew dependencyCheckAnalyze`)
- **Trivy**: Container/filesystem scanning (CI/CD)

### Mutation Testing
- **PITest**: Mutation threshold ≥70%, coverage ≥80%
- **Usage**: `make mutation`

## Suppression Policy

### When to Suppress
1. Framework-specific false positives (Jmix/Vaadin/Lombok)
2. Generated code (Lombok, EclipseLink)
3. Test patterns (reflection, multiple assertions)

### How to Suppress
1. Use specific rule: `@SuppressWarnings("checkstyle:RuleName")`
2. Document why: Add comment explaining suppression
3. Local first: Prefer inline over global configs
4. Framework-only: Only for framework patterns

**Example**:
```java
// Framework pattern: Jmix views don't need serialVersionUID (framework-managed)
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class UserListView extends StandardListView<User> {
    // ...
}
```

## Workflow

### Local Development
```bash
make format         # Format code
make analyze-full   # Check quality
make test           # Run tests
make coverage       # Check coverage
```

### CI/CD Pipeline
1. Format Check: `spotlessCheck`
2. Code Quality: `codeQualityFull` (includes SonarLint)
3. Tests: `test` + coverage verification
4. Security: OWASP + Trivy

All jobs must pass before merge.

## Reports

- **Checkstyle**: `build/reports/checkstyle/`
- **PMD**: `build/reports/pmd/`
- **SpotBugs**: `build/reports/spotbugs/`
- **SonarLint**: `build/reports/sonarlint/`
- **JaCoCo**: `build/reports/jacoco/test/html/index.html`

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Quality gate fails | Check `build/reports/`, run `make analyze-full`, fix root causes |
| Coverage below threshold | Check report, add tests, verify thresholds |
| Suppression needed | Verify framework-specific, document why, prefer fixing code |

## References

- [SDLC Documentation](../workflow/SDLC.md)
- [CI/CD Documentation](CI_CD.md)
- [Contributing Guide](../../CONTRIBUTING.md)
