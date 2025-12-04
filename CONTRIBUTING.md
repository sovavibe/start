# Contributing to Start

Thank you for your interest in contributing to Start! This document provides guidelines and instructions for contributing to the project.

## Code of Conduct

By participating in this project, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md).

## Getting Started

### Prerequisites

Before you begin, ensure you have:

- **Java 21** - [Download](https://adoptium.net/) or [Documentation](https://docs.oracle.com/en/java/javase/21/)
- **Docker** - [Download](https://docs.docker.com/get-docker/) or [Documentation](https://docs.docker.com/)
- **Git** - [Download](https://git-scm.com/downloads)
- **IDE** (recommended): IntelliJ IDEA or VS Code with Java extensions

### Development Setup

1. **Fork and Clone**

   ```bash
   git clone https://github.com/YOUR_USERNAME/start.git
   cd start
   ```

2. **Setup Project**

   ```bash
   make setup
   ```

   This will:
   - Check and install dependencies
   - Configure Git hooks
   - Install npm dependencies
   - Compile the project

3. **Start PostgreSQL**

   ```bash
   make postgres-up
   ```

4. **Run the Application**

   ```bash
   make run
   ```

   Access at: http://localhost:8080

For detailed setup instructions, see [Local Development Guide](docs/getting-started/LOCAL_DEVELOPMENT.md).

## Development Workflow

### Branch Strategy

- **main**: Production-ready code
- **develop**: Integration branch for features
- **feature/***: New features
- **fix/***: Bug fixes
- **docs/***: Documentation updates

### Creating a Branch

```bash
git checkout -b feature/your-feature-name
# or
git checkout -b fix/your-bug-fix
```

### Commit Messages

We follow [Conventional Commits](https://www.conventionalcommits.org/) format:

```
type(scope): description

[optional body]

[optional footer]
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `refactor`: Code refactoring
- `test`: Test additions/changes
- `chore`: Build/tooling changes
- `perf`: Performance improvements
- `style`: Code style changes (formatting)

**Scopes:**
- `jmix`: Jmix framework changes
- `vaadin`: Vaadin UI changes
- `entity`: Entity changes
- `view`: View changes
- `service`: Service layer changes
- `security`: Security changes
- `liquibase`: Database changes
- `ui`: UI changes
- `api`: API changes
- `db`: Database changes
- `config`: Configuration changes
- `test`: Test changes

**Examples:**
```
feat(service): add user validation service
fix(view): resolve null pointer in UserListView
docs(ci): update CI/CD documentation
refactor(service): extract payment calculation logic
```

## Vibe Coding Principles

This project follows **Vibe Coding** philosophy. Please adhere to these principles:

### 1. Minimal Suppressions, Maximum Quality

- **Fix root causes, not symptoms**
- Avoid `@SuppressWarnings` unless absolutely necessary
- Suppress only for framework-specific false positives (Jmix/Vaadin/Lombok)
- Document why suppressions are needed

### 2. Strict Quality Gates

- All quality checks must pass (`ignoreFailures=false`)
- Code coverage: 85% instructions, 75% branches, 90% lines
- Cognitive complexity: ≤10 per method
- Cyclomatic complexity: ≤10 per method
- File length: ≤250 lines

### 3. Production-Ready Code

- Write code as if it's going to production immediately
- Follow senior+ level standards
- Use proper error handling
- Include comprehensive tests

### 4. Framework-Specific Suppressions

Only suppress for:
- **Jmix**: Framework patterns (views, entities, lifecycle methods)
- **Vaadin**: UI component patterns
- **Lombok**: Generated code patterns

See [Suppressions Documentation](docs/quality/suppressions-obosnovaniya.md) for details.

## Code Quality Requirements

### Before Submitting

Run all quality checks locally:

```bash
# Format code
make format

# Run all quality checks
make analyze-full

# Run tests
make test

# Full CI pipeline
make ci
```

### Quality Tools

- **Checkstyle**: Code style (`ignoreFailures=false`, `maxWarnings=0`)
- **PMD**: Static analysis (`ignoreFailures=false`)
- **SpotBugs**: Bug detection (`ignoreFailures=false`)
- **SonarLint**: Code quality (`ignoreFailures=false`)
- **Error-prone**: Compile-time checks
- **JaCoCo**: Coverage verification (85%/75%/90%)

See [Quality Gates Documentation](docs/quality/QUALITY_GATES.md) for comprehensive details.

### Suppression Policy

When you must suppress a warning:

1. **Document why**: Add comment explaining the suppression
2. **Use specific rule**: `@SuppressWarnings("checkstyle:RuleName")`
3. **Framework-only**: Only for Jmix/Vaadin/Lombok false positives
4. **Local first**: Prefer inline `@SuppressWarnings` over global configs

**Example:**
```java
// Framework pattern: Jmix views don't need serialVersionUID (framework-managed)
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class UserListView extends StandardListView<User> {
    // ...
}
```

## Testing Requirements

### Test Coverage

- **Minimum**: 85% instructions, 75% branches, 90% lines
- All new code must have tests
- Tests must pass before PR submission

### Running Tests

```bash
# Run all tests
make test

# Run with coverage
make coverage
```

### Test Structure

- Unit tests: `src/test/java/com/digtp/start/...`
- Integration tests: Use `@ExtendWith({SpringExtension.class, AuthenticatedAsAdmin.class})`
- Architecture tests: Use ArchUnit

See [Test Examples](docs/development/examples/test-example.java) for patterns.

## Pull Request Process

### Before Creating a PR

1. ✅ Code is formatted (`make format`)
2. ✅ All quality checks pass (`make analyze-full`)
3. ✅ All tests pass (`make test`)
4. ✅ Coverage thresholds met
5. ✅ Documentation updated (if needed)
6. ✅ Commit messages follow Conventional Commits

### PR Checklist

- [ ] Code follows Vibe Coding principles
- [ ] All quality gates pass
- [ ] Tests added/updated
- [ ] Documentation updated
- [ ] Commit messages follow convention
- [ ] No merge conflicts
- [ ] PR description explains changes

### PR Description Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
How was this tested?

## Quality Checks
- [ ] All quality gates pass
- [ ] Coverage thresholds met
- [ ] No new suppressions added (or documented if needed)
```

### Review Process

1. Automated checks must pass (CI/CD)
2. Code review by maintainers
3. Quality gate verification
4. Approval required before merge

## Documentation Standards

### Code Documentation

- **JavaDoc**: Required for public APIs
- **Comments**: Explain "why", not "what"
- **README updates**: Update if adding features

### Documentation Files

- Update relevant docs in `docs/` directory
- Follow existing documentation style
- Include code examples where helpful

## SDLC Integration

This project follows Vibe Coding SDLC. See [SDLC Documentation](docs/development/SDLC.md) for:

- Development phases
- Quality gates at each phase
- Best practices
- Workflow integration

## Getting Help

- **Documentation**: Check [docs/](docs/) directory
- **Issues**: [GitHub Issues](https://github.com/sovavibe/start/issues)
- **Discussions**: [GitHub Discussions](https://github.com/sovavibe/start/discussions)

## Recognition

Contributors will be recognized in:
- [AUTHORS.md](AUTHORS.md) (if created)
- Release notes
- Project documentation

Thank you for contributing to Start! 🎉

