# Cursor Setup Summary

Quick reference for setting up Cursor IDE for optimal Vibe Coding experience.

## Quick Setup (5 minutes)

### 1. Configure Team Role

**Cursor Settings** → **Features** → **Team Role**:
- Select your role: **Analyst** / **Developer** / **Reviewer** / **Team Lead**

### 2. Enable Background Agents

**Cursor Settings** → **Features** → **Background Agents**:
- ✅ Enable for: Formatting, import organization
- ⚠️ Disable for: Complex logic changes

### 3. Verify Rules

Check that `.cursor/rules/` directory exists with:
- `core.mdc` (always applied)
- `jmix.mdc`, `vaadin.mdc`, `patterns.mdc`, etc. (context-based)

### 4. Test Configuration

Try a simple prompt:
```
Create a Jmix entity following @jmix.mdc patterns
```

## Files Overview

```
.cursor/
├── rules/              # AI rules (auto-loaded based on context)
│   ├── core.mdc       # Always applied
│   ├── jmix.mdc       # Jmix patterns
│   ├── vaadin.mdc     # Vaadin patterns
│   └── ...
└── BUGBOT.md          # PR review guidelines

.cursorrules           # Quick reference (optional)

docs/
├── CURSOR_OPTIMIZATION.md  # Optimization guide
├── CURSOR_AGENTS.md        # Agents configuration
└── ROLES.md                # Team roles setup
```

## Agent Types

1. **Background Agents**: Auto-formatting, imports
2. **Bugbot**: Automatic PR reviews
3. **Chat Agent**: Interactive help
4. **Composer Agent**: Multi-file generation

## Key Commands

- `make analyze-full` - Quality check (run before completion)
- `make format` - Format code
- `make test` - Run tests

## Documentation

- **Full Guide**: [Cursor Agents](CURSOR_AGENTS.md)
- **Optimization**: [Cursor Optimization](CURSOR_OPTIMIZATION.md)
- **Roles**: [Roles Guide](ROLES.md)

## Troubleshooting

**Rules not working?**
- Check glob patterns in rule files
- Verify YAML frontmatter syntax
- Ensure `core.mdc` has `alwaysApply: true`

**Agents not responding?**
- Restart Cursor
- Check Cursor Settings → Features
- Verify rule file syntax

## Status

✅ **Configuration**: Complete  
✅ **Rules**: Optimized for token efficiency  
✅ **Agents**: Configured for Vibe Coding  
✅ **Documentation**: Comprehensive  

**You're all set!** 🚀
