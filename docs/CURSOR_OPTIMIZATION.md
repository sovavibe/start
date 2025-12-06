# Cursor Optimization for Vibe Coding

This document provides recommendations for optimizing Cursor IDE configuration for maximum efficiency with Vibe Coding principles.

> **Last Updated**: 2025-01-XX  
> **Cursor Version**: Current  
> **Status**: ✅ Based on actual project configuration (`.cursor/rules/` system)
> **Note**: This guide is based on the actual working configuration in this project

## Overview

Cursor uses rules and agents to provide AI-powered assistance. Optimizing these configurations ensures:
- **Token Efficiency**: Rules are loaded only when needed
- **Context Relevance**: Right rules applied to right files
- **Performance**: Faster responses, lower costs
- **Quality**: Consistent code generation aligned with project standards

## Rule Structure

### Current Setup (Recommended)

```
.cursor/
├── rules/                    # PRIMARY configuration system (Cursor reads this)
│   ├── core.mdc              # Always applied (alwaysApply: true in YAML)
│   ├── jmix.mdc              # Jmix-specific (globs: ["**/entity/**/*.java", ...])
│   ├── vaadin.mdc            # Vaadin-specific (globs: ["**/view/**/*.java", ...])
│   ├── patterns.mdc          # Architecture patterns (globs: ["**/service/**/*.java", ...])
│   ├── quality.mdc           # Quality & suppressions (globs: [...])
│   ├── suppress-policy.mdc   # Suppression policy (globs: [...])
│   ├── git.mdc               # Git & CI/CD (globs: ["**/*.md", ...])
│   └── palantir-style-guide.mdc  # Style guide (globs: [...])
└── BUGBOT.md                 # Code review guidelines (used by Bugbot)

.cursorrules                  # Optional reference file (NOT used by Cursor)
```

**Important**: 
- `.cursor/rules/*.mdc` is the **actual configuration system** that Cursor uses
- Each `.mdc` file has YAML frontmatter with `globs`, `version`, optionally `alwaysApply`
- `.cursorrules` is just a markdown file for human reference - Cursor does NOT read it

### Rule Loading Strategy (Based on Actual Project Setup)

1. **Core Rules** (`core.mdc`): Always loaded
   - Has `alwaysApply: true` in YAML frontmatter
   - Contains: Project standards, tech stack, principles, AI assistant behavior, workflow & commands, essential policies

2. **Context-Specific Rules**: Loaded via `globs` patterns in YAML frontmatter
   - Example: `jmix.mdc` has `globs: ["**/entity/**/*.java", ...]`
   - Loads only when editing files matching the patterns
   - Reduces token usage, improves relevance

3. **YAML Frontmatter Format**:
   ```yaml
   ---
   alwaysApply: true  # Optional, for core rules
   globs: ["**/pattern/**/*.java"]  # File patterns
   version: "1.0.0"  # Version tracking
   ---
   ```

4. **Versioning**: All rules have `version` field in frontmatter
   - Track changes
   - Cache invalidation
   - Compatibility checks

## Optimization Recommendations

### 1. Glob Pattern Optimization

**Current Project Setup**: Patterns are well-optimized:

```yaml
# Example from jmix.mdc:
globs: ["**/entity/**/*.java", "**/view/**/*.java", "**/service/**/*.java", ...]

# Example from vaadin.mdc:
globs: ["**/view/**/*.java", "**/view/**/*.xml", "**/*View*.java", ...]
```

**Best Practices** (based on actual project):
- ✅ Keep patterns specific to file types/packages
- ✅ Group related patterns in single rule file
- ✅ Use multiple specific patterns rather than one broad pattern
- ❌ Avoid: `globs: ["**/*.java"]` (too broad, loads for all Java files)

### 2. Rule References (Token Economy)

**Current Project**: Rules reference each other via `@rule-name.mdc` syntax

**Example from core.mdc**:
```markdown
# ✅ Good: Reference instead of copy
See `@jmix.mdc` for Jmix-specific patterns
See `@quality.mdc` for suppression policy
```

**Best Practices**:
- ✅ Use `@rule-name.mdc` references to avoid duplicating content
- ✅ Keep rules focused and reference others when needed
- ❌ Avoid: Copying entire rule content into another rule

### 3. Agent Configuration (Based on Project Setup)

#### Code Review Agents (Bugbot)
- **Purpose**: PR code review
- **Configuration**: `.cursor/BUGBOT.md` (exists in project)
- **Current**: Well-configured with project-specific guidelines
- **How It Works**: Bugbot reads `.cursor/BUGBOT.md` + uses rules from `.cursor/rules/`
- **Recommendations**:
  - Keep BUGBOT.md focused on review criteria
  - Reference rules instead of duplicating
  - Update when project standards change

#### Chat Agents
- **Purpose**: Interactive assistance
- **Configuration**: Rules in `.cursor/rules/` (8 files in project)
- **How It Works**:
  - `core.mdc` always loaded (`alwaysApply: true`)
  - Other rules load based on `globs` patterns when editing matching files
  - Rules can reference each other via `@rule-name.mdc`
- **Recommendations**:
  - Use specific `globs` patterns for efficiency
  - Keep `core.mdc` minimal but comprehensive
  - Use rule references to avoid duplication

### 4. Context Management (From core.mdc)

**Principles** (from actual `core.mdc` in project):
- Search codebase first before asking questions
- Use context7 MCP docs for framework documentation
- Reference existing patterns and conventions

**How It Works in This Project**:
1. **Rule Loading**: Rules load automatically based on `globs` patterns
2. **Core Always**: `core.mdc` always loaded (contains these principles)
3. **Context Rules**: Other rules load when editing matching files
4. **References**: Rules can reference each other via `@rule-name.mdc`

### 5. Rule File Organization

**Current Structure**: ✅ Well-organized

**Recommendations**:
- Keep `core.mdc` focused on universal principles
- Split domain-specific rules (jmix, vaadin) into separate files
- Use glob patterns to load only when needed
- Version all rules for tracking

### 6. Performance Optimization

**Token Efficiency**:
1. ✅ Use rule references (`@rule-name.mdc`) instead of copying
2. ✅ Apply rules only to relevant files (glob patterns)
3. ✅ Keep core.mdc essential-only
4. ✅ Use selective application (see core.mdc → AI ASSISTANT BEHAVIOR)

**Response Speed**:
1. ✅ Specific glob patterns = faster rule matching
2. ✅ Smaller rule files = faster loading
3. ✅ Versioned rules = better caching

### 7. Vibe Coding Alignment

**Principles**:
- Minimal suppressions, maximum code quality
- Production-ready from day one
- Senior+ level standards

**Rule Configuration**:
- ✅ Quality gates enforced in rules
- ✅ Suppression policy clearly defined
- ✅ Framework-specific suppressions only
- ✅ Token economy for efficiency

## Configuration Files

### `.cursorrules` (Optional Reference File)

**Status**: ⚠️ **Optional** - Not part of Cursor's rule system

**Current Project Setup**:
- **Primary System**: `.cursor/rules/*.mdc` files (this is what Cursor uses)
- **Optional File**: `.cursorrules` exists in this project as quick reference only
- **Functionality**: `.cursorrules` does NOT configure Cursor - it's just documentation

**Important Notes**:
- `.cursor/rules/*.mdc` is the **actual configuration system** that Cursor reads
- `.cursorrules` in this project is just a markdown file for human reference
- Cursor does NOT read `.cursorrules` for configuration
- All AI behavior is controlled via `.cursor/rules/*.mdc` files

### Cursor Settings

**Recommended Settings** (via Cursor UI → Settings):
1. **Team Role** (if available in your Cursor version):
   - Select based on your role: Analyst/Developer/Reviewer/Team Lead
   - May affect AI assistant behavior and priorities
   
2. **Background Agents** (if available in your Cursor version):
   - ✅ Enable: Formatting, import organization, simple refactorings
   - ⚠️ Disable: Complex logic changes, architecture decisions
   
3. **Bugbot** (Code Review):
   - Automatically reviews PRs using `.cursor/BUGBOT.md` guidelines
   - Configured via `.cursor/BUGBOT.md` file
   - Works alongside `.cursor/rules/` for context-aware reviews
   
4. **Chat Agent**:
   - Uses rules from `.cursor/rules/` directory
   - Auto-loads rules based on file context (via `globs` patterns in YAML frontmatter)
   - `core.mdc` always loaded (`alwaysApply: true`)
   - Other rules load when editing files matching their `globs` patterns
   
**Note**: Some settings may vary by Cursor version. The rule system (`.cursor/rules/*.mdc`) is the core configuration.

## Best Practices

### For Developers

1. **Rule Awareness**: Understand which rules apply to your files
2. **Reference Usage**: Use `@rule-name.mdc` in prompts when needed
3. **Context First**: Search codebase before asking questions
4. **Quality Gates**: Run `make analyze-full` before completion

### For Analysts

1. **Requirements**: Use Analyst role in Cursor settings
2. **Documentation**: Reference existing docs before creating new
3. **Planning**: Use rules to understand project standards

### For Reviewers

1. **Bugbot**: Review PRs using BUGBOT.md guidelines
2. **Quality Checks**: Verify `make analyze-full` passes
3. **Standards**: Ensure code follows rules in `.cursor/rules/`

## Monitoring & Maintenance

### Rule Updates

1. **Versioning**: Update `version` field when changing rules
2. **Compatibility**: Check if changes affect existing workflows
3. **Documentation**: Update docs when rules change significantly

### Performance Monitoring

1. **Token Usage**: Monitor if rules are too verbose
2. **Response Time**: Check if rule loading is slow
3. **Relevance**: Ensure rules match file contexts correctly

### Quality Metrics

1. **Suppression Count**: Should be minimal (vibe coding)
2. **Linter Pass Rate**: Should be 100% (ignoreFailures=false)
3. **Coverage**: Should meet thresholds (85/75/90)

## Troubleshooting

### Rules Not Applied

1. Check glob patterns match your files
2. Verify rule file syntax (YAML frontmatter)
3. Check Cursor settings (agent mode, team role)

### Too Many Tokens

1. Use rule references instead of copying
2. Split large rules into smaller, focused files
3. Optimize glob patterns to be more specific

### Slow Responses

1. Check rule file sizes (should be <500 lines each)
2. Verify glob patterns are specific (not `**/*.java`)
3. Ensure core.mdc is minimal but comprehensive

## Future Enhancements

### Potential Improvements (Based on Current Cursor Capabilities)

1. **Negative Glob Patterns**: Currently not supported, but can be worked around with specific patterns
2. **Rule Dependencies**: Use `@rule-name.mdc` references for implicit dependencies
3. **Rule Testing**: Manually validate rules against codebase
4. **Metrics**: Monitor token usage and response times manually

### Cursor Features to Watch

1. **Agent Improvements**: Cursor regularly updates AI models and agent capabilities
2. **Rule System**: Current system supports glob patterns, versioning, alwaysApply
3. **Performance**: Rules are cached and loaded efficiently based on file context
4. **Bugbot**: Continuously improved for better code review accuracy

### Keeping Documentation Updated

**When to Update**:
- Cursor releases new features affecting rules/agents
- Project standards change significantly
- New patterns emerge that should be documented
- Performance issues identified with current setup

**How to Stay Current**:
- Check [Cursor Changelog](https://cursor.com/docs) for updates
- Review `.cursor/rules/` version numbers when updating
- Test rule changes in development before committing

## References

### Official Cursor Documentation

- **Main Docs**: [https://cursor.com/docs](https://cursor.com/docs)
- **Rules System**: Configured via `.cursor/rules/*.mdc` files with YAML frontmatter
  - Supports: `alwaysApply`, `globs`, `version` in frontmatter
  - Format: Markdown files with YAML frontmatter
- **Bugbot**: [https://cursor.com/docs/bugbot](https://cursor.com/docs/bugbot) (automatic code review)
  - Configured via `.cursor/BUGBOT.md` file

### Project Configuration (Actual Setup)

This project uses:
- **Rules**: `.cursor/rules/*.mdc` files (8 rule files)
- **Bugbot**: `.cursor/BUGBOT.md` for PR review guidelines
- **Reference**: `.cursorrules` (optional, not used by Cursor)

### Project-Specific

- **Project Rules**: `.cursor/rules/` directory
- **Bugbot Guidelines**: `.cursor/BUGBOT.md`
- **Global Settings**: `.cursorrules` (optional, for quick reference)
- **Quality Gates**: `docs/quality/QUALITY_GATES.md`
- **SDLC**: `docs/workflow/SDLC.md`
- **Roles Guide**: `docs/ROLES.md`

## Related Documentation

For detailed information about Cursor agents, see:
- **[Cursor Agents Guide](CURSOR_AGENTS.md)** - Complete guide to all Cursor agents

## Summary

Your current Cursor configuration is **well-optimized** for vibe coding:

✅ **Token Efficient**: Rules use references (`@rule-name.mdc`), `globs` patterns, selective loading  
✅ **Context Aware**: Rules apply only to relevant files (via `globs` in YAML frontmatter)  
✅ **Quality Focused**: Vibe coding principles embedded in `core.mdc` and other rules  
✅ **Well Organized**: Clear structure (8 rule files), versioning, separation of concerns  
✅ **Production Ready**: All quality gates enforced in rules  
✅ **Actual Setup**: Based on real working configuration (`.cursor/rules/*.mdc` system)

**Key Points**:
1. **Configuration System**: `.cursor/rules/*.mdc` files with YAML frontmatter (this is what Cursor uses)
2. **Rule Loading**: `core.mdc` always loaded, others load via `globs` patterns
3. **References**: Use `@rule-name.mdc` to avoid duplication
4. **Bugbot**: Configured via `.cursor/BUGBOT.md`
5. **Optional**: `.cursorrules` exists but is NOT used by Cursor (just reference)

**Recommendations**:
1. Continue using rule references (`@rule-name.mdc`)
2. Keep `globs` patterns specific in YAML frontmatter
3. Update `version` field when changing rules
4. Maintain `core.mdc` as minimal but comprehensive
5. Update rules when project standards evolve

The configuration is **optimized and production-ready** for vibe coding! 🚀
