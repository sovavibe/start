# Suppression Centralization Plan

## Current State Analysis

### Already Centralized ✅

1. **SonarLint/SonarQube** (`config/sonar-project.properties`):
   - `java:S1948` - Non-serializable fields in View classes (e4)
   - `java:S110` - Too many parents for Jmix views (e5)
   - `java:S2177` - Method name conflict for lifecycle methods (e6)
   - `java:S1172` - Unused method parameters in event handlers (e13)
   - And 10 more framework/test patterns

2. **SpotBugs** (`config/spotbugs/exclude.xml`):
   - EclipseLink entity patterns (EI, ES, etc.)
   - Framework lifecycle methods
   - Test class patterns
   - All documented with justifications

3. **Checkstyle** (`.baseline/checkstyle/custom-suppressions.xml`):
   - `NonSerializableClass` for View classes (lines 142-149)
   - `MissingSerialVersionUID` for View classes (lines 134-140)
   - Lombok patterns
   - Framework patterns

### Can Be Centralized Now ✅

1. **Remove inline `java:S1948`** from View classes:
   - Already centralized in `config/sonar-project.properties` (e4)
   - Files: `LoginView.java`, `UserDetailView.java`
   - Action: Remove inline suppressions, rely on central config

### Cannot Be Centralized (PMD Baseline Limitation) ⚠️

**PMD** does not support centralized suppressions via XML/ruleset files when using Palantir Baseline.

**Reason**: Baseline manages PMD configuration and does not support custom ruleset files. Using `pmd { ruleSetFiles = ... }` would completely override Baseline defaults, which is forbidden per project policy. Gradle PMD plugin does NOT support `suppressionsFile` property (verified via Context7 documentation).

**Current inline PMD suppressions**:
- `PMD.NonSerializableClass` - 4 View classes (LoginView, UserListView, UserDetailView, MainView) - **CANNOT FIX** (Framework requirement)
- `PMD.FieldDeclarationsShouldBeAtStartOfClass` - 1 View class (UserDetailView) - **CANNOT FIX** (Jmix convention)
- `PMD.LongVariable`, `PMD.FormalParameterNamingConventions`, `PMD.MissingSerialVersionUID` - StartApplication - **CANNOT FIX** (Framework interface requirements)
- `PMD.AbstractClassWithoutAbstractMethod` - AbstractIntegrationTest - **ACCEPTABLE** (Standard test base class pattern)
- `PMD.AvoidDuplicateLiterals`, `PMD.TestClassWithoutTestCases` - TestFixtures - **ACCEPTABLE** (Test utility class pattern)

**Impact**: These must remain inline with `@SuppressWarnings("PMD.RuleName")` annotations.

**Test suppressions fixed**:
- `PMD.AvoidAccessibilityAlteration` - **FIXED** (Extracted to ReflectionTestUtils)
- `PMD.AvoidDuplicateLiterals` - **FIXED** (Extracted constants in UserDetailViewTest)
- `PMD.NullAssignment` - **FIXED** (Refactored test cleanup in MainViewTest, UserDetailViewTest)

## Action Plan

### Phase 1: Remove Duplicate Inline Suppressions ✅

1. Remove `java:S1948` inline suppressions from:
   - `src/main/java/com/digtp/start/view/login/LoginView.java` (line 61)
   - `src/main/java/com/digtp/start/view/user/UserDetailView.java` (line 75)

   **Justification**: Already centralized in `config/sonar-project.properties` (e4)

### Phase 2: Fix Test Suppressions ✅

1. **AbstractIntegrationTest**: Extract reflection helper to separate `ReflectionTestUtils` class
   - Created `src/test/java/com/digtp/start/testsupport/ReflectionTestUtils.java`
   - Moved `invokeMethod` to ReflectionTestUtils
   - Added `invokeConstructor` for constructor reflection
   - Updated all test classes to use ReflectionTestUtils

2. **UserDetailViewTest**: Extract duplicate string literals to constants
   - Already had constants, removed `PMD.AvoidDuplicateLiterals` suppression

3. **MainViewTest, UserDetailViewTest**: Refactor null assignment pattern
   - Removed `PMD.NullAssignment` suppression
   - Added comments explaining null assignment in cleanup

4. **LoginViewTest, LoginViewFailureTest, SecurityConstantsTest**: Use ReflectionTestUtils
   - Replaced direct reflection with ReflectionTestUtils.invokeMethod/invokeConstructor
   - Removed `PMD.AvoidAccessibilityAlteration` suppressions

### Phase 3: Document PMD Limitation 📝

1. Update `@suppress-policy.mdc` to clarify PMD Baseline limitation
2. Add note about when PMD inline suppressions are acceptable
3. Document that PMD suppressions cannot be centralized
4. Update this plan with results

## Summary

| Tool | Centralization Status | Notes |
|------|----------------------|-------|
| SonarLint | ✅ Fully centralized | `config/sonar-project.properties` |
| SpotBugs | ✅ Fully centralized | `config/spotbugs/exclude.xml` |
| Checkstyle | ✅ Fully centralized | `.baseline/checkstyle/custom-suppressions.xml` |
| PMD | ⚠️ Inline only | Baseline limitation, cannot centralize |

## Best Practices

1. **Check central config first**: Before adding inline suppression, check if rule is already centralized
2. **Document PMD suppressions**: Always include justification comment for PMD inline suppressions
3. **Minimize PMD suppressions**: Only suppress framework-specific false positives
4. **Review periodically**: When Baseline updates, check if PMD centralization becomes available

## References

- `@suppress-policy.mdc` - Comprehensive suppression guidelines
- `@palantir-baseline-integration.mdc` - Baseline configuration details
- `@ai-false-positives.mdc` - Quick decision guide
- `config/sonar-project.properties` - SonarLint central config
- `config/spotbugs/exclude.xml` - SpotBugs central config
- `.baseline/checkstyle/custom-suppressions.xml` - Checkstyle central config

