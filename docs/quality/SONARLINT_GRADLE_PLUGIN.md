# SonarLint Gradle Plugin

## Overview

SonarLint Gradle Plugin (`name.remal.sonarlint`) provides code quality and security analysis for Java and XML files. It works both locally and in CI/CD without requiring a server connection.

**Key Features**:
- ✅ Works locally without server connection
- ✅ Works in CI/CD pipelines
- ✅ Single source of truth: `sonar-project.properties` (project root)
- ✅ IDE integration: SonarLint IDE plugin automatically reads from project root
- ✅ Framework-specific rule exclusions for Jmix/Vaadin
- ✅ Future-ready: Will support Connected Mode when SonarLint server is available

## Configuration

### Plugin Declaration

**File**: `build.gradle`

```groovy
plugins {
    id 'name.remal.sonarlint' version '7.0.0'
}
```

### Rule Configuration

**File**: `build.gradle`

Rules are automatically extracted from `sonar-project.properties` multicriteria entries. See `build.gradle` for implementation details.

### Single Source of Truth

**File**: `sonar-project.properties` (project root)

All rule exclusions are defined in `sonar-project.properties` using `sonar.issue.ignore.multicriteria` entries. Gradle plugin automatically extracts rules from multicriteria and disables them - no manual synchronization needed.

**Location**: File is in project root for IDE integration (SonarLint IDE plugin automatically reads from root). Symlink in `config/` for backward compatibility.

**Important**: When adding new exclusions:
1. Add multicriteria entry in `sonar-project.properties` (project root)
2. Update `sonar.issue.ignore.multicriteria` list
3. That's it! Gradle plugin automatically picks up the change

## Usage

### Local Development

```bash
# Run SonarLint for main source code
./gradlew sonarlintMain

# Run SonarLint for test code
./gradlew sonarlintTest

# Run both
./gradlew sonarlintMain sonarlintTest

# Or via code quality task (includes other tools)
make analyze
```

### CI/CD Integration

Same commands work in CI/CD pipelines:

```yaml
# GitHub Actions example
- name: Run SonarLint
  run: ./gradlew sonarlintMain sonarlintTest
```

**No server connection required** - works out of the box in any CI/CD environment.

## Reports

Reports are generated in `build/reports/sonarlint/`:
- **HTML**: `build/reports/sonarlint/sonarlintMain.html`
- **XML**: `build/reports/sonarlint/sonarlintMain.xml`

## Configuration Details

### Properties from sonar-project.properties

The plugin automatically loads properties from `sonar-project.properties` (project root):
- Project settings (projectKey, projectName)
- Complexity thresholds
- Source encoding
- Java version
- Rule exclusions (via multicriteria)

### Framework-Specific Exclusions

Exclusions are configured for Jmix/Vaadin framework patterns:
- View classes extending framework base classes
- Lifecycle methods
- UI component injection
- Test-specific patterns

See `sonar-project.properties` (project root) for complete list of exclusions.

## Troubleshooting

### Rules Not Applied

**Problem**: Rules that should be disabled are still shown.

**Solution**:
1. Verify rule is in `sonar-project.properties` (project root) multicriteria
2. Rebuild project: `./gradlew clean build`
3. Gradle plugin automatically extracts rules from multicriteria

### Reports Not Generated

**Problem**: Reports directory is empty.

**Solution**:
1. Run task explicitly: `./gradlew sonarlintMain`
2. Check build output for errors
3. Verify plugin is applied: `./gradlew tasks --all | grep sonarlint`

## Future: SonarLint Server Integration

When SonarLint server becomes available, Connected Mode will be implemented:
- Rules synced from server Quality Profile
- Centralized configuration management
- Team consistency across all developers

**Current status**: Plugin works in standalone mode (no server required).

## IDE Integration

SonarLint IDE plugin (IntelliJ IDEA, VS Code, Eclipse) automatically reads `sonar-project.properties` from project root. No additional configuration needed.

**File location**: `sonar-project.properties` (project root)

## Related Documentation

- **Configuration**: `sonar-project.properties` (project root)
- **Build configuration**: `build.gradle` → `sonarLint` block
- **Quality gates**: `docs/quality/QUALITY_GATES.md`
- **CI/CD**: `docs/ci-cd/CI_CD.md`

