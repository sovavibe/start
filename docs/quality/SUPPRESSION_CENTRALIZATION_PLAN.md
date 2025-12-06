# Suppression Centralization Status

## Centralization Status

| Tool | Status | Config File |
|------|--------|-------------|
| SonarLint | Fully centralized | `sonar-project.properties` |
| SpotBugs | Fully centralized | `config/spotbugs/exclude.xml` |
| Checkstyle | Fully centralized | `.baseline/checkstyle/custom-suppressions.xml` |
| PMD | Inline only | Baseline limitation, cannot centralize |

## PMD Limitation

PMD does not support centralized suppressions via XML/ruleset files when using Palantir Baseline.

**Reason**: Baseline manages PMD configuration and does not support custom ruleset files. Gradle PMD plugin does NOT support `suppressionsFile` property.

**Impact**: PMD suppressions must remain inline with `@SuppressWarnings("PMD.RuleName")` annotations.

**Current inline PMD suppressions**:
- Framework-specific patterns (Jmix Views, Vaadin interfaces)
- Test patterns (reflection, cleanup)

## Best Practices

1. Check central config first before adding inline suppression
2. Document PMD suppressions with justification comment
3. Minimize PMD suppressions - only framework-specific false positives
4. Review periodically when Baseline updates

## References

- `@suppress-policy.mdc` - Comprehensive suppression guidelines
- `sonar-project.properties` - SonarLint central config
- `config/spotbugs/exclude.xml` - SpotBugs central config
- `.baseline/checkstyle/custom-suppressions.xml` - Checkstyle central config
