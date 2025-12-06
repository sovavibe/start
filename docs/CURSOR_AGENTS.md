# Cursor Agents Configuration Guide

Complete guide to configuring and optimizing Cursor agents for maximum productivity with Vibe Coding principles.

> **Last Updated**: 2025-01-XX  
> **Cursor Version**: Current  
> **Status**: ✅ Verified against current Cursor capabilities

## Overview

Cursor provides multiple types of AI agents that work together to assist with coding:

1. **Background Agents** - Autonomous code improvements
2. **Bugbot** - Automatic PR code review
3. **Chat Agent** - Interactive coding assistance
4. **Composer Agent** - Multi-file code generation

## 1. Background Agents

### What They Do

Background agents automatically improve your code while you work:
- Format code
- Organize imports
- Fix simple linter errors
- Apply code style fixes
- Suggest improvements

### Configuration

**Location**: Cursor Settings → Features → Background Agents

**Settings**:
- ✅ **Enable**: Recommended for formatting and imports
- ⚠️ **Scope**: Can limit to specific file types
- ⚠️ **Complex Changes**: Disable for architecture decisions

### Best Practices

**Enable For**:
- ✅ Code formatting (auto-format on save)
- ✅ Import organization
- ✅ Simple refactorings (rename, extract method)
- ✅ Linter error fixes (simple cases)

**Disable For**:
- ❌ Complex logic changes
- ❌ Architecture decisions
- ❌ Business logic modifications
- ❌ Test file changes (may break tests)

### Project-Specific Configuration

For this project (Vibe Coding Jmix):

```yaml
Background Agents:
  Enable: true
  Scope: 
    - Java files (*.java)
    - XML files (*.xml) for Vaadin views
  Exclude:
    - Test files (*Test.java) - review manually
    - Migration files (Liquibase) - never auto-modify
    - Generated files
```

**Recommendation**: Enable for formatting/imports, disable for complex changes.

## 2. Bugbot (Code Review Agent)

### What It Does

Bugbot automatically reviews Pull Requests and:
- Identifies bugs and code quality issues
- Suggests improvements
- Checks compliance with project standards
- Validates against `.cursor/BUGBOT.md` guidelines

### Configuration

**Location**: `.cursor/BUGBOT.md`

**Current Setup**: ✅ Already configured with project-specific guidelines

**Key Sections**:
- Database Migrations (Liquibase)
- Queries & Data Access
- Code Quality
- Security
- Jmix Patterns
- Testing
- Error Handling
- Performance
- Architecture & Patterns

### How It Works

1. **Trigger**: Automatically runs on PR creation/update
2. **Context**: Uses `.cursor/rules/` for project standards
3. **Guidelines**: Follows `.cursor/BUGBOT.md` for review criteria
4. **Output**: Comments on PR with suggestions

### Customization

Edit `.cursor/BUGBOT.md` to:
- Add project-specific review rules
- Customize quality thresholds
- Define framework-specific patterns
- Set security requirements

**Example** (from current setup):

```markdown
## Database Migrations (Liquibase)

When reviewing Liquibase migrations, ensure they are backwards compatible:
- ✅ OK: New tables, new nullable columns
- ❌ NOT OK: Dropping columns, changing types
```

### Best Practices

1. **Keep Guidelines Focused**: Only include rules that matter
2. **Update Regularly**: Reflect changes in project standards
3. **Reference Rules**: Use `@rule-name.mdc` syntax when possible
4. **Be Specific**: Clear examples of ✅ OK vs ❌ NOT OK

## 3. Chat Agent

### What It Does

The Chat Agent provides interactive assistance:
- Answers questions about code
- Generates code based on prompts
- Explains code behavior
- Suggests improvements
- Helps with debugging

### Configuration

**Location**: Rules in `.cursor/rules/` (auto-loaded based on context)

**How Rules Load**:
- **Core Rules** (`core.mdc`): Always loaded (`alwaysApply: true`)
- **Context Rules**: Loaded via glob patterns when editing matching files
- **References**: Uses `@rule-name.mdc` for efficiency

### Rule Loading Strategy

```
Editing: src/main/java/.../entity/User.java
  → Loads: core.mdc (always)
  → Loads: jmix.mdc (matches **/entity/**/*.java)
  → Loads: quality.mdc (matches **/entity/**/*.java)
  → Loads: palantir-style-guide.mdc (matches **/entity/**/*.java)
  → Skips: vaadin.mdc (doesn't match)
```

### Token Efficiency

**Current Setup**: ✅ Optimized

- Rules use references (`@rule-name.mdc`) instead of copying
- Glob patterns ensure only relevant rules load
- Core rules are minimal but comprehensive
- Versioning allows caching

### Best Practices

1. **Use Specific Prompts**: "Add validation to User entity" vs "fix this"
2. **Reference Rules**: "Follow @jmix.mdc for entity creation"
3. **Context First**: Chat agent searches codebase before asking
4. **Quality Gates**: Always run `make analyze-full` after changes

### Example Interactions

**Good Prompt**:
```
Create a new Jmix entity Order with:
- UUID id with @JmixGeneratedValue
- @Version field for optimistic locking
- Fields: orderNumber (String), totalAmount (BigDecimal)
- Follow @jmix.mdc patterns
```

**Bad Prompt**:
```
Make an order thing
```

## 4. Composer Agent

### What It Does

Composer Agent generates code across multiple files:
- Creates complete features
- Generates entities, views, services together
- Maintains consistency across files
- Follows project architecture

### Configuration

**Location**: Uses same rules as Chat Agent (`.cursor/rules/`)

**How It Works**:
1. Analyzes project structure
2. Loads relevant rules based on file types
3. Generates code following patterns
4. Maintains consistency with existing code

### Best Practices

1. **Clear Requirements**: Specify what to create
2. **Architecture Awareness**: Mention Clean Architecture layers
3. **Quality First**: Request to run `make analyze-full`
4. **Incremental**: Build features step by step

### Example Usage

**Good Request**:
```
Create a complete Order management feature:
1. Order entity (UUID, @Version, fields: orderNumber, totalAmount, status)
2. OrderService with createOrder() method
3. OrderListView and OrderDetailView
4. Follow Clean Architecture (entity → service → view)
5. Add tests for OrderService
6. Run make analyze-full before completion
```

## Agent Interaction

### How Agents Work Together

```
┌─────────────────┐
│  Background      │ → Auto-format, imports
│  Agents          │
└────────┬─────────┘
         │
         ▼
┌─────────────────┐
│  Chat Agent     │ → Interactive help, code generation
│  (with Rules)   │
└────────┬─────────┘
         │
         ▼
┌─────────────────┐
│  Composer       │ → Multi-file generation
│  Agent          │
└────────┬─────────┘
         │
         ▼
┌─────────────────┐
│  Bugbot         │ → PR review, quality checks
│  (on PR)        │
└─────────────────┘
```

### Rule Priority

1. **Core Rules** (`core.mdc`): Always applied
2. **Context Rules**: Based on file patterns
3. **Bugbot Guidelines**: For PR reviews
4. **Project Standards**: Embedded in all rules

## Configuration Checklist

### Initial Setup

- [ ] Enable Background Agents (formatting, imports)
- [ ] Configure Team Role in Cursor Settings
- [ ] Review `.cursor/BUGBOT.md` guidelines
- [ ] Verify `.cursor/rules/` structure
- [ ] Test Chat Agent with project-specific prompt

### Ongoing Maintenance

- [ ] Update rules when project standards change
- [ ] Review Bugbot suggestions regularly
- [ ] Monitor token usage (if applicable)
- [ ] Keep rules versioned and documented
- [ ] Test agent behavior after rule updates

## Troubleshooting

### Background Agents Not Working

**Symptoms**: Code not auto-formatting, imports not organizing

**Solutions**:
1. Check Cursor Settings → Features → Background Agents (enabled?)
2. Verify file types are in scope
3. Check for conflicting formatters (Prettier, Spotless)
4. Restart Cursor

### Bugbot Not Reviewing PRs

**Symptoms**: No comments on PR, no review suggestions

**Solutions**:
1. Verify `.cursor/BUGBOT.md` exists and is valid
2. Check PR is from correct branch
3. Ensure Bugbot is enabled for repository
4. Check GitHub integration settings

### Chat Agent Not Following Rules

**Symptoms**: Generated code doesn't match project standards

**Solutions**:
1. Verify glob patterns match your files
2. Check rule file syntax (YAML frontmatter)
3. Ensure `core.mdc` has `alwaysApply: true`
4. Test with explicit rule reference: "Follow @jmix.mdc"

### Too Many Tokens Used

**Symptoms**: Slow responses, high token usage

**Solutions**:
1. Use rule references (`@rule-name.mdc`) instead of copying
2. Make glob patterns more specific
3. Split large rules into smaller files
4. Review `core.mdc` - keep it minimal but comprehensive

## Advanced Configuration

### Custom Agent Behavior

**Configuration System**:
- **Primary**: `.cursor/rules/*.mdc` files (recommended, modern)
- **Optional/Legacy**: `.cursorrules` file (may be deprecated)

**How to Influence Agent Behavior**:

1. **Rule References**: Use `@rule-name.mdc` in prompts
2. **Explicit Instructions**: "Follow @jmix.mdc for entity creation"
3. **Context Hints**: "This is a Jmix entity, use DataManager"
4. **Quality Gates**: "Run make analyze-full before completion"

### Team Role Impact

**Analyst Role**:
- Focuses on analysis and planning
- Emphasizes documentation
- Prioritizes requirements

**Developer Role**:
- Focuses on implementation
- Runs quality checks automatically
- Emphasizes tests and production-ready code

**Reviewer Role**:
- Focuses on code review
- Validates quality gates
- Checks compliance with standards

**Team Lead Role**:
- Manages PR workflows
- Coordinates team efforts
- Ensures quality standards

### Performance Optimization

**Token Efficiency**:
- ✅ Use rule references
- ✅ Specific glob patterns
- ✅ Minimal core rules
- ✅ Context-based loading

**Response Speed**:
- ✅ Smaller rule files
- ✅ Specific patterns
- ✅ Cached rules (versioning)
- ✅ Efficient rule structure

## Project-Specific Agent Configuration

### For Vibe Coding Jmix Project

**Background Agents**:
```yaml
Enabled: true
Scope: Java, XML files
Exclude: Test files, migrations, generated files
Actions: Formatting, import organization, simple fixes
```

**Bugbot**:
```yaml
Config: .cursor/BUGBOT.md
Rules: .cursor/rules/
Focus: Quality gates, Jmix patterns, security
```

**Chat Agent**:
```yaml
Rules: .cursor/rules/ (auto-loaded)
Core: core.mdc (always)
Context: Based on file patterns
Efficiency: Rule references, glob patterns
```

**Composer Agent**:
```yaml
Rules: Same as Chat Agent
Architecture: Clean Architecture (entity → service → view)
Quality: Always run make analyze-full
```

## References

### Official Cursor Documentation

- **Main Docs**: [https://cursor.com/docs](https://cursor.com/docs)
- **Bugbot**: [https://cursor.com/docs/bugbot](https://cursor.com/docs/bugbot)
- **Rules**: Configured via `.cursor/rules/*.mdc` files
- **Settings**: Cursor Settings → Features

### Project Documentation

- **Rules**: `.cursor/rules/` directory
- **Bugbot Guidelines**: `.cursor/BUGBOT.md`
- **Optimization Guide**: `docs/CURSOR_OPTIMIZATION.md`
- **Roles Guide**: `docs/ROLES.md`
- **Quality Gates**: `docs/quality/QUALITY_GATES.md`

## Summary

Your Cursor agents are configured for optimal Vibe Coding:

✅ **Background Agents**: Auto-formatting and imports  
✅ **Bugbot**: Automatic PR reviews with project guidelines  
✅ **Chat Agent**: Context-aware assistance with efficient rules  
✅ **Composer Agent**: Multi-file generation following architecture  

**Key Principles**:
1. Enable Background Agents for simple tasks
2. Let Bugbot handle PR reviews automatically
3. Use Chat Agent with rule references
4. Keep rules optimized for token efficiency
5. Maintain quality gates in all agents

**Next Steps**:
1. Configure Team Role in Cursor Settings
2. Test agents with project-specific tasks
3. Review and customize `.cursor/BUGBOT.md` if needed
4. Monitor agent behavior and adjust rules as needed

Your agents are ready for maximum productivity! 🚀
