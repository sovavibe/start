# Suppressions Explained

Suppressions (`@SuppressWarnings`) tell linters: "I know this looks like a problem, but it's acceptable for this case."

**Important**: Suppressions are used ONLY when necessary (framework patterns, tests), NOT to hide real problems.

## Statistics

- **Total suppressions**: 20 (inline in code)
- **Centralized rules**: 14 (in config files)
- **Files with suppressions**: 15

## Production Code Suppressions (6)

### PMD.NonSerializableClass (4 occurrences)
**Files**: `LoginView.java`, `MainView.java`, `UserListView.java`, `UserDetailView.java`

**Reason**: Jmix Views contain framework-managed non-serializable beans (MessageBundle, UI components). Framework manages serialization.

**Example**:
```java
@SuppressWarnings("PMD.NonSerializableClass")
public class LoginView extends StandardView {
    @ViewComponent
    private MessageBundle messageBundle; // Framework-managed, not serializable
}
```

### PMD.FieldDeclarationsShouldBeAtStartOfClass (1 occurrence)
**File**: `UserDetailView.java`

**Reason**: Jmix convention: 1) constructor-injected fields, 2) @ViewComponent fields. Cannot change order.

### PMD.LongVariable, PMD.FormalParameterNamingConventions (1 occurrence)
**File**: `StartApplication.java`

**Reason**: Vaadin interface requirements - cannot change parameter names.

### unused (Error Prone) (1 occurrence)
**File**: `LocaleHelper.java`

**Reason**: Interface `LocaleChangeObserver` requires `LocaleChangeEvent` parameter even if unused.

### java:S1948 (2 occurrences)
**Files**: `LoginView.java`, `UserDetailView.java`

**Reason**: Framework-managed fields don't need serialization. Also centralized in `sonar-project.properties`.

## Test Code Suppressions (14)

### PMD.AvoidAccessibilityAlteration (6 occurrences)
**Files**: Multiple test files

**Reason**: Reflection for accessing private methods in tests is acceptable. Standard test pattern.

### PMD.AbstractClassWithoutAbstractMethod (1 occurrence)
**File**: `AbstractIntegrationTest.java`

**Reason**: Standard pattern for test base classes providing common functionality.

### PMD.NullAssignment (1 occurrence)
**File**: `MainViewTest.java`

**Reason**: Test cleanup pattern - assigning null after entity removal.

### PMD.TypeParameterUnusedInFormals (1 occurrence)
**File**: `MainViewTest.java`

**Reason**: Generic type parameter for type safety in reflection helpers.

### PMD.AvoidDuplicateLiterals (3 occurrences)
**Files**: Test files

**Reason**: Duplicate string literals in tests improve readability. Test data should be obvious.

### PMD.TestClassWithoutTestCases (1 occurrence)
**File**: `TestFixtures.java`

**Reason**: Utility class for test data, not a test class.

### PMD.CommentRequired, PMD.CommentDefaultAccessModifier (2 occurrences)
**File**: `ArchitectureTest.java`

**Reason**: ArchUnit convention - descriptive rule names are self-documenting.

### java:S5738 (1 occurrence)
**File**: `LoginViewFailureTest.java`

**Reason**: `@MockBean` is deprecated but standard Spring Boot pattern for test mocking.

### PreferSafeLogger (1 occurrence)
**File**: `AuditService.java`

**Reason**: Audit logger requires specific logger name for separate log file configuration.

## Centralized Suppressions (14 rules)

### SonarLint (`sonar-project.properties`)
- `java:S1948` - Non-serializable fields in Views
- `java:S110` - Too many parents for Jmix views
- `java:S2177` - Method name conflict in lifecycle methods
- `java:S1172` - Unused method parameters in event handlers
- `java:S1186` - MissingJavadocMethod for Lombok-generated methods
- `java:S2150` - DesignForExtension for framework classes
- `java:S6813` - Field injection for @ViewComponent and @Value
- And 7 more framework/test patterns

### Checkstyle (`.baseline/checkstyle/custom-suppressions.xml`)
- `NonSerializableClass` - For Views (framework-managed dependencies)
- `MissingSerialVersionUID` - For Views, Application, Entities (Jmix manages serialization)

### SpotBugs (`config/spotbugs/exclude.xml`)
- EclipseLink entity patterns
- Framework lifecycle methods
- Test class patterns

## When to Suppress

**Use suppressions when:**
- Framework pattern (Jmix, Vaadin, Spring Boot)
- Test code (reflection, cleanup)
- Interface requirement (unused parameters)

**Do NOT use suppressions when:**
- Real code problem (fix the code!)
- Inconvenient rule (better to fix code)
- Temporary solution (fix the code!)

## How to Suppress

1. **Always add comment** - Explain why suppression is needed
2. **Check central config** - May already be configured
3. **Verify false positive** - Don't hide real problems

**Good example**:
```java
@SuppressWarnings("PMD.NonSerializableClass") // Jmix View: contains framework-managed non-serializable beans
public class LoginView extends StandardView {
    // ...
}
```

**Bad example**:
```java
@SuppressWarnings("PMD.UnusedLocalVariable") // No comment - bad!
public void method() {
    int unused = 5; // Better: remove variable or use it
}
```

## Summary

- **20 inline suppressions** - All justified and documented
- **14 centralized rules** - For common patterns
- **All suppressions** - Only for framework patterns and tests, not real problems

**Remember**: Suppressions are not a way to hide problems, but a way to tell linters "I know what I'm doing" for justified cases.
