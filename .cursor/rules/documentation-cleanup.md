# DOCUMENTATION CLEANUP RULES

## ❌ FILES TO DELETE (Duplicates)

### Enterprise Documentation (3 files → 1 file)
- ❌ DELETE: `docs/quality/enterprise-complete.md` (duplicate)
- ❌ DELETE: `docs/quality/enterprise-implemented.md` (duplicate)
- ✅ KEEP: `docs/quality/ENTERPRISE-READY.md` (most complete)

### Plans (completed, no longer needed)
- ❌ DELETE: `docs/quality/enterprise-improvements-plan.md` (plan is completed)

### Lombok Documentation (consolidate)
- ❌ DELETE: `docs/quality/lombok-quick-reference.md` (covered in lombok-best-practices.md)
- ✅ KEEP: `docs/quality/lombok-best-practices.md` (main guide)

## ✅ FILES TO KEEP

### Essential Documentation
- ✅ `docs/quality/lombok-best-practices.md` - main Lombok guide
- ✅ `docs/quality/lombok-checkstyle-integration.md` - integration guide
- ✅ `docs/quality/lombok-migration-examples.md` - migration examples
- ✅ `docs/quality/ENTERPRISE-READY.md` - enterprise status
- ✅ `docs/quality/enterprise-logging.md` - logging guide
- ✅ `docs/quality/configuration-profiles.md` - profiles guide
- ✅ `docs/quality/spring-annotations-guide.md` - annotations guide
- ✅ `docs/quality/intellij-javadoc-setup.md` - IDE setup
- ✅ `docs/quality/suppressions-analysis-2025.md` - suppressions analysis

## RULES FOR FUTURE

- ❌ NEVER create duplicate documentation files
- ❌ NEVER create "plan" files (create only when implementing)
- ❌ NEVER create "quick reference" if main guide exists
- ✅ Update existing files instead of creating new ones
- ✅ One topic = one file maximum

