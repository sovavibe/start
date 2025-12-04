# PMD Configuration

## Status: Using Palantir Baseline Defaults

This project uses **Palantir Baseline defaults for PMD** - no custom ruleset is applied.

## Why No Custom Ruleset?

- ✅ **Baseline provides curated defaults** optimized for Spring/enterprise projects
- ✅ **Framework patterns (Jmix/Lombok) are already considered** in Baseline
- ✅ **Suppressions via `@SuppressWarnings` are local and visible** in code
- ✅ **Easier to maintain** - less configuration, automatic updates with Baseline

## Framework-Specific Suppressions

Framework-specific false positives are handled via **local `@SuppressWarnings("PMD.RuleName")`** annotations in code with justification comments.

### Common Patterns

- **Jmix Views**: Lifecycle methods, `@ViewComponent` fields, serialization
- **Lombok**: `@RequiredArgsConstructor`, `@Getter/@Setter` generated code
- **Spring Boot**: `@Bean` methods, configuration classes
- **Tests**: Reflection, null assignments, duplicate literals

### Example

```java
@SuppressWarnings({
    "PMD.CommentSize", // Copyright header is standard and required
    "PMD.MissingSerialVersionUID" // Jmix views don't need serialVersionUID
})
public class UserDetailView extends StandardDetailView<User> {
    // ...
}
```

## Historical Note

The file `custom-ruleset.xml` previously existed but was removed because:
- ❌ `ruleSetFiles` completely overrides Baseline defaults
- ❌ Loses all Baseline advantages
- ❌ Duplicates rules and configurations

## Why No Centralized Exclusions?

**Palantir Baseline Limitation**: PMD is managed by Baseline, and using `ruleSetFiles` or `excludePattern` would completely override Baseline's curated defaults. This would:
- Lose all Baseline advantages (optimized rules for Spring/enterprise)
- Require manual maintenance of ruleset
- Break automatic Baseline updates

**Solution**: Use inline `@SuppressWarnings("PMD.RuleName")` for framework-specific false positives. This is the recommended approach by Baseline.

See: `.cursor/rules/suppress-policy.mdc` for suppression guidelines.

