# Extending Checkstyle Configuration

## Overview

`.baseline/checkstyle/checkstyle.xml` can be extended with custom rules, but Baseline overwrites it during `baselineUpdateConfig`. This document explains how to manually maintain custom rules.

## Approach: Manual Configuration in Git

**Why manual?**
- ✅ **Transparency**: All changes visible in git diff
- ✅ **Control**: You decide when to update Baseline config
- ✅ **Review**: Changes can be reviewed in PRs
- ✅ **Simplicity**: No complex Gradle tasks

## Workflow

### Step 1: Update Baseline Configuration

```bash
./gradlew baselineUpdateConfig
```

This will overwrite `.baseline/checkstyle/checkstyle.xml` with latest Baseline defaults.

### Step 2: Add Custom Rules Manually

Edit `.baseline/checkstyle/checkstyle.xml` and add your custom rules **before** `<!-- Stricter checks end -->`:

```xml
        <module name="RegexpSinglelineJava">
            <property name="format" value="(void setUp\(\))|(void setup\(\))|(void setupStatic\(\))|(void setUpStatic\(\))|(void beforeTest\(\))|(void teardown\(\))|(void tearDown\(\))|(void beforeStatic\(\))|(void afterStatic\(\))"/>
            <property name="message" value="Test setup/teardown methods are called before(), beforeClass(), after(), afterClass(), but not setUp, teardown, etc."/>
        </module>

        <!-- Add your custom rules here, before "Stricter checks end" -->
        <module name="RegexpSinglelineJava"> <!-- Custom: Your Rule Name -->
            <property name="format" value="your-pattern"/>
            <property name="message" value="Your message"/>
            <property name="ignoreComments" value="true"/>
            <property name="ignoreStrings" value="true"/>
        </module>

        <!-- Stricter checks end -->
```

### Step 3: Commit Changes

```bash
git add .baseline/checkstyle/checkstyle.xml
git commit -m "feat(checkstyle): add custom rule for X"
```

### Step 4: Verify

```bash
./gradlew checkstyleMain
```

## Example Custom Rules

### FQN (Fully Qualified Names) Check

```xml
<module name="RegexpSinglelineJava"> <!-- Custom: Avoid fully qualified names (FQN) -->
    <property name="format" value="[a-z][a-zA-Z0-9]*\.[a-z][a-zA-Z0-9]*\.[a-zA-Z][a-zA-Z0-9]*"/>
    <property name="message" value="Avoid fully qualified names. Use imports instead."/>
    <property name="ignoreComments" value="true"/>
    <property name="ignoreStrings" value="true"/>
</module>
```

**Note**: This pattern may cause parsing errors. Consider using a custom Checkstyle check instead (see `CUSTOM_CHECKSTYLE_RULES.md`).

### Non-ASCII Characters Check

```xml
<module name="RegexpSinglelineJava"> <!-- Custom: Non-ASCII characters -->
    <property name="format" value="[^\u0000-\u007F]"/>
    <property name="message" value="Avoid non-ASCII characters in code (excluding comments and strings)."/>
    <property name="ignoreComments" value="true"/>
    <property name="ignoreStrings" value="true"/>
</module>
```

**Note**: Unicode patterns in XML require proper escaping. Test thoroughly.

## Best Practices

1. **Place rules before `<!-- Stricter checks end -->`** - This keeps them organized
2. **Add descriptive comments** - `<!-- Custom: Rule Name -->` helps identify custom rules
3. **Test after Baseline updates** - Run `./gradlew checkstyleMain` after `baselineUpdateConfig`
4. **Document in PR** - Explain why custom rules are needed
5. **Keep rules minimal** - Only add rules that can't be handled by AI rules or code review

## When to Use Custom Rules

✅ **Use custom rules for:**
- Project-specific patterns that Baseline doesn't cover
- Rules that need automatic enforcement in CI/CD
- Patterns that are hard to catch in code review

❌ **Don't use custom rules for:**
- Framework-specific suppressions (use `custom-suppressions.xml`)
- Patterns already covered by AI rules
- Rules that cause too many false positives

## Alternative: Custom Checkstyle Checks

For complex rules, consider creating custom Checkstyle checks in `buildSrc`:
- See `docs/quality/CUSTOM_CHECKSTYLE_RULES.md` for examples
- More maintainable than complex regex patterns
- Better error messages and context

## Configuration Files in Git

The following Baseline files are version-controlled:
- ✅ `.baseline/checkstyle/checkstyle.xml` - Main config (can be extended)
- ✅ `.baseline/checkstyle/custom-suppressions.xml` - Custom suppressions (never overwritten)
- ✅ `.baseline/copyright/*.txt` - Copyright templates

The following are auto-generated (not in git):
- ❌ `.baseline/checkstyle/checkstyle-suppressions.xml` - Auto-generated suppressions

## Troubleshooting

**Problem**: Custom rules disappear after `baselineUpdateConfig`
- **Solution**: Re-add them manually and commit to git

**Problem**: Rule causes XML parsing errors
- **Solution**: Check XML escaping, especially for regex patterns with special characters

**Problem**: Too many false positives
- **Solution**: Use `ignoreComments` and `ignoreStrings` properties, or add suppressions in `custom-suppressions.xml`

