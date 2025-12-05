# Test Simplification Research

## Current State

**Reflection Usage in Tests:**
- `ReflectionTestUtils.invokeMethod()` - used to call private methods
- `getEditedEntity()` - called via reflection on `StandardDetailView`
- `generateUserName()`, `isSubstituted()`, `createAvatar()` - called via reflection on `MainView`
- `localeChange()` - called via reflection on `LoginView`
- `onLogin()` - called via reflection on `LoginView`

## Research Findings

### 1. Jmix Official Testing Patterns

**From Jmix Documentation (Context7):**

Official example shows:
```java
UserDetailView userDetailView = UiTestUtils.getCurrentView(); // Direct cast!
```

**Key Finding**: Jmix allows direct casting of `View<?>` to concrete View types in tests!

### 2. StandardDetailView.getEditedEntity()

**Finding**: `getEditedEntity()` is a **public method** in `StandardDetailView<T>`.

**Current code**:
```java
final User editedEntity = ReflectionTestUtils.invokeMethod(
    User.class, view, "getEditedEntity", new Class<?>[0]);
```

**Can be simplified to**:
```java
final UserDetailView detailView = (UserDetailView) view;
final User editedEntity = detailView.getEditedEntity();
```

### 3. MainView Private Methods

**Methods tested via reflection**:
- `generateUserName(User user)` - **private** method
- `isSubstituted(User user)` - **@VisibleForTesting** (package-private)
- `createAvatar(String name)` - **@VisibleForTesting** (package-private)
- `userMenuButtonRenderer(UserDetails)` - **@Install** method (public)
- `userMenuHeaderRenderer(UserDetails)` - **@Install** method (public)

**Analysis**:
- `@VisibleForTesting` methods are package-private - **CAN be called directly** in tests!
- `@Install` methods are public - **CAN be called directly**!
- Only `generateUserName()` is truly private - **NEEDS reflection or make package-private**

### 4. LoginView Methods

**Methods tested via reflection**:
- `localeChange(LocaleChangeEvent)` - **public** (implements `LocaleChangeObserver`)
- `onLogin(LoginEvent)` - **@Subscribe** method (package-private by default)

**Analysis**:
- `localeChange()` is public interface method - **CAN be called directly**!
- `onLogin()` is package-private - **CAN be called directly** from test package!

## Simplification Opportunities

### High Priority (Easy Wins)

1. **getEditedEntity()** - Replace reflection with direct cast:
   ```java
   // Before
   final User editedEntity = ReflectionTestUtils.invokeMethod(
       User.class, view, "getEditedEntity", new Class<?>[0]);
   
   // After
   final UserDetailView detailView = (UserDetailView) view;
   final User editedEntity = detailView.getEditedEntity();
   ```

2. **@VisibleForTesting methods** - Call directly:
   ```java
   // Before
   final boolean isSubstituted = ReflectionTestUtils.invokeMethod(
       Boolean.class, mainView, "isSubstituted", new Class<?>[] {User.class}, user);
   
   // After
   final MainView mainView = (MainView) getMainView();
   final boolean isSubstituted = mainView.isSubstituted(user);
   ```

3. **Public interface methods** - Call directly:
   ```java
   // Before
   ReflectionTestUtils.invokeMethod(
       Void.class, loginView, "localeChange", new Class<?>[] {LocaleChangeEvent.class}, event);
   
   // After
   final LoginView loginView = (LoginView) getCurrentViewAsView();
   loginView.localeChange(event);
   ```

4. **@Install methods** - Call directly:
   ```java
   // Before
   final Component component = ReflectionTestUtils.invokeMethod(
       Component.class, mainView, "userMenuButtonRenderer", new Class<?>[] {UserDetails.class}, user);
   
   // After
   final MainView mainView = (MainView) getMainView();
   final Component component = mainView.userMenuButtonRenderer(user);
   ```

### Medium Priority (Requires Code Changes)

1. **generateUserName()** - Make package-private or add @VisibleForTesting:
   ```java
   // Option 1: Make package-private
   String generateUserName(final User user) { ... }
   
   // Option 2: Add @VisibleForTesting
   @VisibleForTesting
   String generateUserName(final User user) { ... }
   ```

2. **onLogin()** - Already package-private, can call directly from test package

### Low Priority (Keep Reflection)

1. **Truly private methods** that cannot be made accessible
2. **Framework lifecycle methods** that are not meant to be called directly

## Implementation Plan

### Phase 1: Direct Casts (No Code Changes)

1. Replace `getEditedEntity()` reflection with direct cast
2. Replace `@VisibleForTesting` method calls with direct calls
3. Replace public interface method calls with direct calls
4. Replace `@Install` method calls with direct calls

### Phase 2: Code Accessibility (Requires Production Code Changes)

1. Make `generateUserName()` package-private or add `@VisibleForTesting`
2. Verify `onLogin()` can be called directly (already package-private)

### Phase 3: Remove ReflectionTestUtils (If Not Needed)

1. If all reflection removed, consider removing `ReflectionTestUtils`
2. Keep only for truly private methods that cannot be made accessible

## Benefits

1. **Simpler tests** - No reflection, direct method calls
2. **Better IDE support** - Autocomplete, refactoring, navigation
3. **Type safety** - Compile-time checks instead of runtime
4. **Better readability** - Clear method calls instead of string-based reflection
5. **No PMD suppressions** - No need for `PMD.AvoidAccessibilityAlteration`

## Risks

1. **Class loader issues** - Jmix test environment may have class loader problems
2. **View casting** - Need to verify casting works in test environment
3. **Package structure** - Tests must be in same package for package-private access

## Verification Steps

1. Test direct casting: `(UserDetailView) view`
2. Test package-private method calls
3. Test @VisibleForTesting method calls
4. Verify all tests pass
5. Remove reflection where possible
6. Update documentation

