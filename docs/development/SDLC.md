# Software Development Life Cycle (SDLC) for Vibe Coding

This document describes the Software Development Life Cycle (SDLC) integrated with Vibe Coding principles for the Start project.

## Overview

Vibe Coding SDLC emphasizes quality at every phase, with strict quality gates and minimal suppressions. The goal is production-ready code from day one.

## SDLC Phases

### 1. Planning Phase

**Objectives:**
- Define requirements
- Design architecture
- Plan quality gates

**Activities:**
- Requirements gathering
- Architecture design
- Quality gate planning
- Risk assessment

**Deliverables:**
- Requirements document
- Architecture design
- Quality gate checklist

**Vibe Coding Integration:**
- Plan for minimal suppressions
- Identify framework-specific patterns early
- Design for testability

### 2. Development Phase

**Objectives:**
- Write production-ready code
- Follow Vibe Coding principles
- Maintain quality standards

**Activities:**
- Code implementation
- Code formatting (`make format`)
- Local quality checks (`make analyze-full`)
- Unit test writing

**Quality Gates:**
- Code must compile without warnings
- Formatting must pass (`spotlessCheck`)
- Local quality checks must pass
- Unit tests must pass

**Vibe Coding Principles:**
- **Minimal Suppressions**: Fix root causes, not symptoms
- **Framework-Specific Only**: Suppress only for Jmix/Vaadin/Lombok
- **Production-Ready**: Write code as if deploying immediately
- **Document Suppressions**: Explain why suppressions are needed

**Commands:**
```bash
# Format code
make format

# Check quality locally
make analyze-full

# Run tests
make test
```

### 3. Testing Phase

**Objectives:**
- Ensure code quality
- Verify functionality
- Meet coverage thresholds

**Activities:**
- Unit testing
- Integration testing
- Coverage verification
- Mutation testing (optional)

**Quality Gates:**
- **Coverage Thresholds**:
  - Instructions: 85%
  - Branches: 75%
  - Lines: 90%
- All tests must pass
- No failing tests allowed

**Vibe Coding Integration:**
- Tests must be production-quality
- Test edge cases and error conditions
- Use proper test patterns (see [Test Examples](examples/test-example.java))

**Commands:**
```bash
# Run tests
make test

# Check coverage
make coverage

# Mutation testing
make mutation
```

### 4. Code Review Phase

**Objectives:**
- Ensure code quality
- Verify Vibe Coding compliance
- Share knowledge

**Activities:**
- Pull request creation
- Code review
- Quality gate verification
- Documentation review

**Quality Gates:**
- All CI/CD checks must pass
- Code review approval required
- Quality gates verified
- Documentation updated

**Vibe Coding Checklist:**
- [ ] No unnecessary suppressions
- [ ] Suppressions documented
- [ ] Framework-specific suppressions only
- [ ] Root causes fixed, not symptoms
- [ ] Production-ready code
- [ ] Tests added/updated
- [ ] Coverage thresholds met

**PR Requirements:**
- Follow [Conventional Commits](https://www.conventionalcommits.org/)
- Include description of changes
- Link related issues
- Ensure all checks pass

### 5. Integration Phase

**Objectives:**
- Integrate changes
- Verify system stability
- Maintain quality

**Activities:**
- Merge to integration branch
- Integration testing
- Quality gate verification
- System testing

**Quality Gates:**
- All CI/CD jobs must pass
- Integration tests must pass
- No regressions
- Quality metrics maintained

**Vibe Coding Integration:**
- Maintain quality standards
- Monitor quality metrics
- Address any quality degradation

### 6. Deployment Phase

**Objectives:**
- Deploy to production
- Monitor system health
- Ensure stability

**Activities:**
- Build artifacts
- Deploy to environment
- Health checks
- Monitoring setup

**Quality Gates:**
- Build must succeed
- Health checks must pass
- No critical issues
- Monitoring active

**Vibe Coding Integration:**
- Production-ready code (already verified)
- Proper error handling
- Comprehensive logging
- Observability enabled

## Quality Gates at Each Phase

### Development Phase Gates

| Gate | Tool | Threshold | Command |
|------|------|-----------|---------|
| Formatting | Spotless | Must pass | `make format-check` |
| Checkstyle | Checkstyle | `maxWarnings=0` | `make analyze-full` |
| PMD | PMD | `ignoreFailures=false` | `make analyze-full` |
| SpotBugs | SpotBugs | `ignoreFailures=false` | `make analyze-full` |
| SonarLint | SonarLint | `ignoreFailures=false` | `make analyze-full` |

### Testing Phase Gates

| Gate | Tool | Threshold | Command |
|------|------|-----------|---------|
| Unit Tests | JUnit 5 | All pass | `make test` |
| Coverage (Instructions) | JaCoCo | ≥85% | `make coverage` |
| Coverage (Branches) | JaCoCo | ≥75% | `make coverage` |
| Coverage (Lines) | JaCoCo | ≥90% | `make coverage` |

### Code Review Phase Gates

| Gate | Tool | Threshold | Location |
|------|------|-----------|----------|
| CI/CD Pipeline | GitHub Actions | All jobs pass | `.github/workflows/ci.yml` |
| SonarCloud Quality Gate | SonarCloud | Must pass | SonarCloud dashboard |
| Security Scan | OWASP, Trivy | No critical issues | CI/CD pipeline |

## Vibe Coding Workflow

```
1. Planning
   ↓
2. Development
   ├─ Format code (make format)
   ├─ Check quality (make analyze-full)
   └─ Write tests
   ↓
3. Testing
   ├─ Run tests (make test)
   └─ Verify coverage (make coverage)
   ↓
4. Code Review
   ├─ Create PR
   ├─ CI/CD checks
   └─ Review approval
   ↓
5. Integration
   ├─ Merge to develop
   └─ Integration tests
   ↓
6. Deployment
   ├─ Build artifacts
   └─ Deploy to production
```

## Best Practices

### During Development

1. **Run Quality Checks Frequently**
   ```bash
   make analyze-full
   ```

2. **Format Code Before Committing**
   ```bash
   make format
   ```

3. **Write Tests Alongside Code**
   - Don't defer testing
   - Test edge cases
   - Maintain coverage

4. **Fix Issues Immediately**
   - Don't accumulate technical debt
   - Address quality gate failures
   - Fix root causes

### During Code Review

1. **Verify Quality Gates**
   - Check CI/CD status
   - Review SonarCloud results
   - Verify coverage

2. **Check Suppressions**
   - Are they necessary?
   - Are they documented?
   - Are they framework-specific?

3. **Review Tests**
   - Are tests comprehensive?
   - Do they cover edge cases?
   - Is coverage maintained?

### During Integration

1. **Monitor Quality Metrics**
   - Track coverage trends
   - Monitor complexity
   - Watch for regressions

2. **Address Issues Promptly**
   - Fix failing tests
   - Resolve quality gate failures
   - Update documentation

## Quality Metrics

### Code Quality

- **Cognitive Complexity**: ≤10 per method
- **Cyclomatic Complexity**: ≤10 per method
- **File Length**: ≤250 lines
- **Code Duplication**: <3%

### Test Quality

- **Coverage**: 85%/75%/90% (instructions/branches/lines)
- **Test Quality**: All tests must be meaningful
- **Mutation Score**: ≥70% (if using PIT)

### Security

- **Vulnerabilities**: Zero critical/high
- **Dependencies**: Up to date
- **Security Scan**: Pass all checks

## Tools and Commands

### Development

```bash
# Setup project
make setup

# Format code
make format

# Check quality
make analyze-full

# Run tests
make test

# Check coverage
make coverage
```

### CI/CD

- **Format Check**: `./gradlew spotlessCheck`
- **Quality Check**: `./gradlew codeQualityFull`
- **Tests**: `./gradlew test`
- **Coverage**: `./gradlew jacocoTestReport`
- **SonarCloud**: `./gradlew sonar`

## Continuous Improvement

### Regular Reviews

- Review quality metrics monthly
- Update quality gates as needed
- Refine Vibe Coding principles
- Share learnings with team

### Documentation Updates

- Keep SDLC documentation current
- Update quality gate thresholds
- Document new patterns
- Share best practices

## References

- [Vibe Coding Principles](../CONTRIBUTING.md#vibe-coding-principles)
- [Quality Gates Documentation](QUALITY_GATES.md)
- [CI/CD Documentation](CI_CD.md)
- [Contributing Guide](../CONTRIBUTING.md)

