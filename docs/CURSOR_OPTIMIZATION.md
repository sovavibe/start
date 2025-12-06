# Cursor Optimization for Vibe Coding

This document provides recommendations for optimizing Cursor IDE configuration for maximum efficiency with Vibe Coding principles.

> **Last Updated**: 2025-01-XX  
> **Cursor Version**: Current (as of document creation)  
> **Status**: ✅ Verified against current Cursor capabilities

## Overview

Cursor uses rules and agents to provide AI-powered assistance. Optimizing these configurations ensures:
- **Token Efficiency**: Rules are loaded only when needed
- **Context Relevance**: Right rules applied to right files
- **Performance**: Faster responses, lower costs
- **Quality**: Consistent code generation aligned with project standards

## Rule Structure

### Current Setup

```
.cursor/
├── rules/
│   ├── core.mdc              # Always applied (alwaysApply: true)
│   ├── jmix.mdc              # Jmix-specific (glob patterns)
│   ├── vaadin.mdc            # Vaadin-specific (glob patterns)
│   ├── patterns.mdc          # Architecture patterns (glob patterns)
│   ├── quality.mdc           # Quality & suppressions (glob patterns)
│   ├── suppress-policy.mdc   # Suppression policy (glob patterns)
│   ├── git.mdc               # Git & CI/CD (glob patterns)
│   └── palantir-style-guide.mdc  # Style guide (glob patterns)
└── BUGBOT.md                 # Code review guidelines
```

### Rule Loading Strategy

1. **Core Rules** (`core.mdc`): Always loaded (`alwaysApply: true`)
   - Project standards, tech stack, principles
   - AI assistant behavior
   - Workflow & commands
   - Essential policies (logging, testing, security)

2. **Context-Specific Rules**: Loaded via glob patterns
   - Only when editing matching files
   - Reduces token usage
   - Improves relevance

3. **Versioning**: All rules have `version` field
   - Track changes
   - Cache invalidation
   - Compatibility checks

## Optimization Recommendations

### 1. Glob Pattern Optimization

**Current**: Patterns are well-optimized, but can be improved:

```yaml
# ✅ Good: Specific patterns
globs: ["**/entity/**/*.java", "**/view/**/*.java"]

# ✅ Better: More specific when possible
globs: ["**/entity/**/*.java", "**/view/**/*.java", "**/service/**/*.java"]

# ❌ Avoid: Too broad
globs: ["**/*.java"]  # Loads for all Java files
```

**Recommendations**:
- Keep patterns specific to file types/packages
- Use negative patterns if needed (not currently supported, but plan for future)
- Group related patterns in single rule file

### 2. Rule References (Token Economy)

**Current**: Rules reference each other via `@rule-name.mdc` syntax

**Best Practices**:
```markdown
# ✅ Good: Reference instead of copy
See `@jmix.mdc` for Jmix-specific patterns

# ❌ Bad: Copying entire content
[Full jmix.mdc content here...]
```

**Optimization**: Continue using references to minimize token usage.

### 3. Agent Configuration

Cursor supports different agent types:

#### Background Agents
- **Purpose**: Autonomous code improvements
- **Configuration**: Via Cursor settings
- **Recommendations**:
  - Enable for: Formatting, import organization, simple refactorings
  - Disable for: Complex logic changes, architecture decisions
  - Scope: Limit to specific file types if needed

#### Code Review Agents (Bugbot)
- **Purpose**: PR code review
- **Configuration**: `.cursor/BUGBOT.md`
- **Current**: Well-configured with project-specific guidelines
- **Recommendations**:
  - Keep BUGBOT.md focused on review criteria
  - Reference rules instead of duplicating
  - Update when project standards change

#### Chat Agents
- **Purpose**: Interactive assistance
- **Configuration**: Rules in `.cursor/rules/`
- **Recommendations**:
  - Rules auto-loaded based on context
  - Use specific glob patterns for efficiency
  - Keep core.mdc minimal but comprehensive

### 4. Context Management

**Principles** (from core.mdc):
- Search codebase first before asking questions
- Use context7 MCP docs for framework documentation
- Reference existing patterns and conventions

**Optimizations**:
1. **Scoped Search**: Use semantic search with specific directories
2. **Incremental Context**: Load only relevant rules via `@filename.mdc`
3. **File References**: Use `@filepath` for specific files instead of entire directories
4. **Pattern Matching**: Search for patterns before asking questions

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

### `.cursorrules` (Optional)

You can create a `.cursorrules` file in project root for global settings. This file provides:
- Quick reference for AI assistant
- Project overview and principles
- Links to detailed rules

**Current Setup**: ✅ Already configured in project root (`.cursorrules`)

**Note**: 
- `.cursorrules` is a simple markdown file for quick reference
- Detailed rules in `.cursor/rules/*.mdc` are more powerful (glob patterns, versioning, alwaysApply)
- Both can coexist: `.cursorrules` for overview, `.cursor/rules/` for detailed configuration

### Cursor Settings

**Recommended Settings** (via Cursor UI → Settings):
1. **Team Role** (Features → Team Role):
   - Select based on your role: Analyst/Developer/Reviewer/Team Lead
   - Affects AI assistant behavior and priorities
   
2. **Background Agents** (Features → Background Agents):
   - ✅ Enable: Formatting, import organization, simple refactorings
   - ⚠️ Disable: Complex logic changes, architecture decisions
   - Scope: Can limit to specific file types
   
3. **Bugbot** (Code Review):
   - Automatically reviews PRs using `.cursor/BUGBOT.md` guidelines
   - Configured via `.cursor/BUGBOT.md` file
   - Works alongside `.cursor/rules/` for context-aware reviews
   
4. **Chat Agent**:
   - Full access to rules in `.cursor/rules/`
   - Auto-loads rules based on file context (glob patterns)
   - Uses rule references (`@rule-name.mdc`) for efficiency
   
5. **Context Window**: Use default (rules are optimized for token efficiency)

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
- **Rules System**: Rules are configured via `.cursor/rules/*.mdc` files with YAML frontmatter
- **Bugbot**: [https://cursor.com/docs/bugbot](https://cursor.com/docs/bugbot) (automatic code review)
- **Team Roles**: Configured via Cursor Settings → Features → Team Role
- **Background Agents**: Configured via Cursor Settings → Features → Background Agents

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

✅ **Token Efficient**: Rules use references, glob patterns, selective loading  
✅ **Context Aware**: Rules apply only to relevant files  
✅ **Quality Focused**: Vibe coding principles embedded in rules  
✅ **Well Organized**: Clear structure, versioning, separation of concerns  
✅ **Production Ready**: All quality gates enforced  

**Recommendations**:
1. Continue using rule references (`@rule-name.mdc`)
2. Keep glob patterns specific
3. Monitor token usage and response times
4. Update rules when project standards evolve
5. Use Team Role settings for role-specific behavior

The configuration is already **super optimal** for vibe coding! 🚀
