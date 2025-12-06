# Cursor Configuration

Documentation based on actual working configuration in this project.

## Configuration System

Cursor uses `.cursor/rules/*.mdc` files for configuration.

### File Structure

```
.cursor/
├── rules/              # Configuration files (Cursor reads these)
│   ├── core.mdc        # Always loaded (alwaysApply: true)
│   ├── jmix.mdc        # Loads for Jmix files (globs patterns)
│   ├── vaadin.mdc      # Loads for Vaadin files (globs patterns)
│   ├── patterns.mdc    # Loads for service/view files (globs patterns)
│   ├── quality.mdc    # Loads for entity/view/service files (globs patterns)
│   ├── suppress-policy.mdc  # Loads for entity/view/service files (globs patterns)
│   ├── git.mdc        # Loads for .md/.java/.gradle files (globs patterns)
│   └── palantir-style-guide.mdc  # Loads for entity/service/view files (globs patterns)
└── BUGBOT.md          # Bugbot PR review guidelines
```

### Rule File Format

Each `.mdc` file has YAML frontmatter:

```yaml
---
alwaysApply: true    # Optional: always load this rule
globs: ["**/pattern/**/*.java"]  # File patterns to match
version: "1.0.0"     # Version number
---

# Rule content in Markdown
```

### How Rules Load

1. **core.mdc**: Always loaded (has `alwaysApply: true`)
2. **Other rules**: Load when editing files matching `globs` patterns
3. **Example**: Editing `src/main/java/.../entity/User.java` loads:
   - `core.mdc` (always)
   - `jmix.mdc` (matches `**/entity/**/*.java`)
   - `quality.mdc` (matches `**/entity/**/*.java`)
   - `palantir-style-guide.mdc` (matches `**/entity/**/*.java`)

### Rule References

Rules can reference each other:
```markdown
See `@jmix.mdc` for Jmix-specific patterns
```

## Bugbot Configuration

Bugbot uses `.cursor/BUGBOT.md` for PR code review.

- Automatically reviews PRs
- Uses guidelines from `.cursor/BUGBOT.md`
- Also uses rules from `.cursor/rules/` for context

## Files NOT Used by Cursor

- **`.cursorrules`**: This file exists but Cursor does NOT read it. It's just a reference file.

## Project Rules

Current rules in `.cursor/rules/`:

1. **core.mdc** - Project standards, principles, AI behavior, workflow
2. **jmix.mdc** - Jmix framework patterns (entities, views, data access)
3. **vaadin.mdc** - Vaadin UI patterns
4. **patterns.mdc** - Architecture patterns, error handling, async
5. **quality.mdc** - Quality standards, suppression policy
6. **suppress-policy.mdc** - When and how to suppress warnings
7. **git.mdc** - Git conventions, CI/CD
8. **palantir-style-guide.mdc** - Code style guide

## Quick Reference

**Configuration**: `.cursor/rules/*.mdc` files  
**Bugbot**: `.cursor/BUGBOT.md`  
**Format**: Markdown with YAML frontmatter  
**Loading**: `core.mdc` always, others via `globs` patterns

## Documentation

- **This file**: Actual working configuration
- **Roles**: `docs/ROLES.md` - Role-specific commands
- **Quality Gates**: `docs/quality/QUALITY_GATES.md` - Quality standards
