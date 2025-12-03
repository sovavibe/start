# Code Examples and Templates

This directory contains code examples and templates for common patterns used in the project.

## Purpose

These examples serve as:
- **Copy-paste templates** for creating new components
- **Reference implementations** following best practices
- **AI adaptation guides** for code generation
- **Onboarding materials** for new developers

## Examples

### Service Example (`service-example.java`)

Template for creating Spring service classes following best practices:
- `@Service`, `@RequiredArgsConstructor`, `@Slf4j`
- Constructor injection (not field injection)
- `@Transactional` for database operations
- `@Nullable/@NonNull` annotations for null-safety
- Parameterized logging
- `Objects.equals()` for null-safe comparisons
- `String.formatted()` for modern Java string formatting

**Reference**: `src/main/java/com/digtp/start/service/UserService.java`

### Entity Example (`entity-example.java`)

Template for creating JPA/Jmix entity classes:
- `@JmixEntity`, `@Entity`, `@Table`
- `@JmixGeneratedValue`, `@Version` (REQUIRED for optimistic locking)
- `@InstanceName` for display name
- Bean Validation (`@NotNull`, `@Email`, `@Size`)
- `@EqualsAndHashCode(onlyExplicitlyIncluded=true)`
- `@ToString(onlyExplicitlyIncluded=true)`
- `List.copyOf()` for immutable collections
- Text blocks and `String.formatted()`

**Reference**: `src/main/java/com/digtp/start/entity/User.java`

### View Example (`view-example.java`)

Template for creating Jmix/Vaadin view classes:
- `@Route`, `@ViewController`, `@ViewDescriptor`
- `@EditedEntityContainer` for data binding
- `@ViewComponent` for UI components
- `@Subscribe` for lifecycle events
- Constructor injection for services
- Static final fields for cached data
- Parameterized logging

**Reference**: `src/main/java/com/digtp/start/view/user/UserDetailView.java`

### Test Example (`test-example.java`)

Template for creating integration tests:
- `@SpringBootTest`, `@ExtendWith`, `@ActiveProfiles("test")`
- AAA/GWT pattern (Arrange-Act-Assert / Given-When-Then)
- AssertJ for fluent assertions
- `@AfterEach` for cleanup
- Unique test data (timestamps, UUIDs)
- Test naming: `test<MethodUnderTest>_<state>`

**Reference**: 
- `src/test/java/com/digtp/start/service/UserServiceTest.java`
- `src/test/java/com/digtp/start/security/StartPasswordValidatorTest.java`

## Best Practices

All examples follow best practices from:
- **Palantir**: Palantir Baseline, strict metrics, centralized suppressions
- **Google**: Java Style Guide, Error Prone, NullAway, modern Java features
- **Amazon**: Clean Architecture, SOLID, test coverage, performance
- **Meta**: Immutability, final fields, modern Java features

### Key Principles

1. **No Suppressions**: Code should be clean without `@SuppressWarnings` (except justified cases)
2. **Null-Safety**: Use `@Nullable/@NonNull` and `Objects.equals()`
3. **Immutability**: Prefer immutable objects, use `List.copyOf()`
4. **Modern Java**: Use Java 21 features (pattern matching, text blocks, `String.formatted()`)
5. **Logging**: Parameterized logging (no string concatenation)
6. **Testing**: AAA/GWT pattern, AssertJ, cleanup in `@AfterEach`

## Usage

1. **Copy** the relevant example file
2. **Replace** package names and TODO comments
3. **Adapt** to your specific requirements
4. **Follow** the best practices outlined in comments
5. **Reference** the actual implementation files for complete examples

## References

- [Palantir Java Style Guide](https://github.com/palantir/gradle-baseline/blob/develop/docs/java-style-guide/readme.md)
- [Jmix Documentation](https://docs.jmix.io/)
- [Vaadin Flow Documentation](https://vaadin.com/docs/latest/flow)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [AssertJ Documentation](https://assertj.github.io/doc/)

## Project Structure

```
src/main/java/com/digtp/start/
├── service/          # Business logic services
├── entity/           # JPA/Jmix entities
├── view/             # Jmix/Vaadin views
└── config/           # Configuration classes

src/test/java/com/digtp/start/
├── service/          # Service tests
├── entity/           # Entity tests
└── view/             # View tests
```

## Quality Gates

All code must pass:
- ✅ Checkstyle (Palantir Baseline)
- ✅ PMD (custom ruleset)
- ✅ SpotBugs (minimal exclusions)
- ✅ Error Prone (strict checks)
- ✅ NullAway (null-safety)
- ✅ Tests (85% instruction, 75% branch, 90% line coverage)

## Support

For questions or improvements, see:
- `.cursor/rules/` - Cursor rules and best practices
- `config/` - Linter configurations
- Actual implementation files - Complete working examples

