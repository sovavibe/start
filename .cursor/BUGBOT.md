# Bugbot Review Guidelines

> **Reference**: [Bugbot Documentation](https://cursor.com/ru/docs/bugbot)  
> **Purpose**: This file contains project-specific guidelines for Bugbot code reviews. Bugbot automatically analyzes PRs and leaves comments with bug fixes and quality improvements.  
> **Usage**: Bugbot uses these guidelines along with `.cursor/rules/` to provide context-aware code reviews.

## Database Migrations (Liquibase)

When reviewing Liquibase migrations, ensure they are backwards compatible and follow Jmix best practices:

### Backwards Compatibility

- ✅ **OK**: New tables, new nullable columns, new columns with defaults
- ✅ **OK**: Removing foreign keys and constraints
- ✅ **OK**: Removing indices
- ✅ **OK**: Making non-null column nullable
- ✅ **OK**: Increasing VARCHAR/TEXT column size

- ❌ **NOT OK**: Adding foreign keys (handle in application layer via Jmix DataManager)
- ❌ **NOT OK**: Dropping columns (mark as deprecated in entity with `@Deprecated` annotation instead)
- ❌ **NOT OK**: Renaming columns (use `@Column(name = "old_name")` to maintain compatibility)
- ❌ **NOT OK**: Changing column types (except increasing VARCHAR size)
- ❌ **NOT OK**: Changing default values on existing columns
- ❌ **NOT OK**: Dropping tables

### Index Creation

- ⚠️ **WARN**: New indices on existing tables must be created with `CONCURRENTLY` keyword (PostgreSQL)
- ⚠️ **WARN**: Index creation should be isolated to its own migration file
- ⚠️ **FLAG**: All index creations require careful review for production impact

### Schema Design

- ✅ **Prefer**: `BIGINT` / `BIGSERIAL` over `INTEGER` / `SERIAL` for IDs (UUID preferred via `@JmixGeneratedValue`)
- ✅ **Prefer**: `TEXT` over `VARCHAR` for variable-length strings
- ❌ **NOT OK**: Foreign keys in database (handle via Jmix DataManager)
- ❌ **NOT OK**: Cascading deletes in database (handle in application layer)
- ✅ **Required**: `@Version` field for optimistic locking (Jmix requirement)
- ✅ **Required**: Explicit `@JoinColumn(name)` for relationships

### Migration Rules

- ✅ **Naming**: `V{version}__{description}.xml` or `{timestamp}__{description}.xml`
- ✅ **Naming**: Tables in UPPER_SNAKE_CASE (e.g., `USER_` not `USER`)
- ✅ **Isolation**: Migrations should be isolated to their own PR when possible
- ❌ **NEVER**: Modify applied changelogs
- ✅ **Always**: Use incremental changesets
- ✅ **Always**: Specify indexes on foreign keys (if using explicit FK)
- ✅ **Always**: Use explicit `@JoinColumn(name)` for relationships
- ✅ **Always**: Include `@Version` field for optimistic locking

### Jmix-Specific

- ✅ **Required**: `@JmixEntity` annotation on entities
- ✅ **Required**: `@JmixGeneratedValue` for UUID primary keys
- ✅ **Required**: `@Version` for optimistic locking
- ✅ **Use**: `dataManager.create()` instead of `new Entity()`
- ❌ **NOT OK**: Direct EntityManager usage (except native SQL for health checks)

## Queries & Data Access

- ❌ **NOT OK**: Queries without indexes (full table scans)
- ❌ **NOT OK**: `GROUP BY` or `JOIN` in queries (handle in application layer via Jmix DataManager)
- ❌ **NOT OK**: `OFFSET`/`LIMIT` on large tables (use cursor-based pagination via indexed column)
- ✅ **Use**: FetchPlan to avoid N+1 queries
- ✅ **Use**: `@Transactional(readOnly = true)` for read-only queries
- ✅ **Use**: DataManager for all entity operations (create, save, load, remove)
- ❌ **NOT OK**: `new Entity()` (use `dataManager.create()`)
- ❌ **NOT OK**: Direct EntityManager usage (except native SQL for health checks)
- ✅ **Use**: PropertyConditions for query building
- ✅ **Use**: Query cache for frequently accessed, rarely changed data

## Code Quality

### Metrics & Limits

- ❌ **NOT OK**: Files >250 lines (refactor: extract methods, split files)
- ❌ **NOT OK**: Cyclomatic complexity >10 (split method)
- ❌ **NOT OK**: Cognitive complexity >10 (simplify logic)
- ✅ **Required**: Formatting: 120 chars/line (Palantir Style Guide)
- ✅ **Required**: Run `make analyze-full` before PR (Checkstyle, PMD, SpotBugs, SonarLint)

### TODO/FIXME Comments

If any changed file contains `/(?:^|\s)(TODO|FIXME)(?:\s*:|\s+)/`, then:
- Add a non-blocking Bug titled "TODO/FIXME comment found"
- Body: "Replace TODO/FIXME with a tracked issue reference, e.g., `TODO(#1234): ...`, or remove it."
- If the TODO already references an issue pattern `/#\d+|[A-Z]+-\d+/`, mark the Bug as resolved automatically.

### Security

- ❌ **NOT OK**: Exposed secrets, passwords, API keys in code
- ❌ **NOT OK**: Unsafe API calls without authentication
- ❌ **NOT OK**: Missing `@Secret` annotation on sensitive entity fields
- ✅ **Required**: `@Secret` on password fields
- ✅ **Required**: Parameterized queries (JPQL, not string concatenation)
- ✅ **Required**: `PasswordEncoder.encode()` before saving passwords
- ✅ **Required**: Input validation at all layers (UI, service, entity)
- ✅ **Required**: Security headers in SecurityFilterChain (HSTS, CSP, X-Frame-Options)
- ❌ **NOT OK**: Logging passwords, tokens, secrets, PII without masking

### Jmix Patterns

- ❌ **NOT OK**: `new Entity()` (use `dataManager.create()`)
- ❌ **NOT OK**: `@Autowired` fields in services (use constructor injection)
- ❌ **NOT OK**: `@Transactional` in views (use services)
- ❌ **NOT OK**: Missing `@Version` on entities
- ❌ **NOT OK**: Missing `@JmixGeneratedValue` on ID fields
- ❌ **NOT OK**: Lazy collections accessed outside transaction
- ❌ **NOT OK**: N+1 queries (use FetchPlan)
- ✅ **Required**: `@JmixEntity` annotation on entities
- ✅ **Required**: `@JmixGeneratedValue` for UUID primary keys
- ✅ **Required**: Explicit `@JoinColumn(name)` for relationships
- ✅ **Use**: DataContext for aggregate editing (StandardDetailView)
- ✅ **Use**: FetchPlan for eager loading of relationships

## Testing

- ✅ **Required**: Tests for new features
- ❌ **NOT OK**: Deleting failing tests (fix root cause instead)
- ✅ **Use**: `@SpringBootTest` for integration tests
- ✅ **Use**: `@ExtendWith({SpringExtension.class, AuthenticatedAsAdmin.class})` for authenticated tests
- ✅ **Cleanup**: `@AfterEach` with `dataManager.remove()` for test data

## Error Handling

- ❌ **NOT OK**: `catch (Exception e) { }` (silent swallowing)
- ❌ **NOT OK**: `e.printStackTrace()` (use `log.error()`)
- ❌ **NOT OK**: Generic `RuntimeException` (use specific exceptions)
- ✅ **Use**: Jmix built-in exceptions (`EntityAccessException`, `OptimisticLockException`, `ConstraintViolationException`)
- ✅ **Use**: `log.error("Operation failed: entity={}, error={}", entityId, e.getMessage(), e)` with context
- ✅ **Handle**: `OptimisticLockException` - reload entity, notify user, allow retry

## Performance

- ❌ **NOT OK**: Loading all entities to memory (use pagination)
- ❌ **NOT OK**: N+1 queries (use FetchPlan)
- ✅ **Use**: Query cache for frequently accessed, rarely changed data
- ✅ **Use**: Batch operations (`saveAll`, `removeAll`) for multiple entities
- ✅ **Use**: Lazy loading in DataGrid with virtual scrolling
- ✅ **Index**: Foreign keys and frequently filtered columns

## Architecture & Patterns

- ✅ **Required**: Clean Architecture layers (Presentation → Business → Infrastructure)
- ✅ **Required**: Business logic in services (not views)
- ❌ **NOT OK**: Business logic in controllers (use services)
- ✅ **Use**: DTOs for API responses (never expose entities directly)
- ✅ **Use**: Constructor injection (not field injection)
- ✅ **Use**: Final fields, immutable objects where possible
- ✅ **Required**: AAA/GWT pattern (Arrange-Act-Assert / Given-When-Then) with explicit comments
- ✅ **Use**: AssertJ for assertions (`assertThat(actual).isEqualTo(expected)`)
- ✅ **Use**: Testcontainers for integration tests (PostgreSQL with reuse)
- ✅ **Use**: Unique test data identifiers (`"test-user-" + System.currentTimeMillis()`)
- ✅ **Coverage**: ≥75% instructions, ≥65% branches, ≥75% lines (JaCoCo)

