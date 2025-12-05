# Test Simplification Plan - Remove Reflection

## Research Summary

**Finding**: Most reflection usage can be eliminated by using direct method calls and type casting.

**Key Discoveries**:
1. Tests are in the same package as production code → package-private methods accessible
2. `@VisibleForTesting` methods are package-private → can call directly
3. `getEditedEntity()` is public → can call after casting View
4. `onLogin()`, `localeChange()` are public → can call directly
5. Jmix official examples use direct casting: `UserDetailView view = UiTestUtils.getCurrentView()`

## Current Reflection Usage

### UserDetailViewTest
- `getEditedEntity()` - **CAN SIMPLIFY** (public method, cast View to UserDetailView)

### MainViewTest  
- `generateUserName()` - **CAN SIMPLIFY** (@VisibleForTesting = package-private)
- `isSubstituted()` - **CAN SIMPLIFY** (@VisibleForTesting = package-private)
- `createAvatar()` - **CAN SIMPLIFY** (@VisibleForTesting = package-private)
- `userMenuButtonRenderer()` - **CAN SIMPLIFY** (@Install = public)
- `userMenuHeaderRenderer()` - **CAN SIMPLIFY** (@Install = public)

### LoginViewTest
- `localeChange()` - **CAN SIMPLIFY** (public interface method)

### LoginViewFailureTest
- `onLogin()` - **CAN SIMPLIFY** (public method)

## Simplification Strategy

### Pattern 1: Direct Cast + Public Method Call

**Before**:
```java
final User editedEntity = ReflectionTestUtils.invokeMethod(
    User.class, view, "getEditedEntity", new Class<?>[0]);
```

**After**:
```java
final UserDetailView detailView = (UserDetailView) view;
final User editedEntity = detailView.getEditedEntity();
```

### Pattern 2: Direct Call to @VisibleForTesting Methods

**Before**:
```java
final boolean isSubstituted = ReflectionTestUtils.invokeMethod(
    Boolean.class, mainView, "isSubstituted", new Class<?>[] {User.class}, user);
```

**After**:
```java
final MainView mainView = (MainView) getMainView();
final boolean isSubstituted = mainView.isSubstituted(user);
```

### Pattern 3: Direct Call to Public Interface Methods

**Before**:
```java
ReflectionTestUtils.invokeMethod(
    Void.class, loginView, "localeChange", new Class<?>[] {LocaleChangeEvent.class}, event);
```

**After**:
```java
final LoginView loginView = (LoginView) getCurrentViewAsView();
loginView.localeChange(event);
```

### Pattern 4: Direct Call to @Install Methods

**Before**:
```java
final Component component = ReflectionTestUtils.invokeMethod(
    Component.class, mainView, "userMenuButtonRenderer", new Class<?>[] {UserDetails.class}, user);
```

**After**:
```java
final MainView mainView = (MainView) getMainView();
final Component component = mainView.userMenuButtonRenderer(user);
```

## Implementation Steps

1. **Replace getEditedEntity() calls** (10 occurrences in UserDetailViewTest)
2. **Replace MainView method calls** (14 occurrences in MainViewTest)
3. **Replace localeChange() call** (1 occurrence in LoginViewTest)
4. **Replace onLogin() calls** (4 occurrences in LoginViewFailureTest)
5. **Remove ReflectionTestUtils** (if no longer needed)
6. **Update AbstractIntegrationTest** (remove deprecated invokeMethod)
7. **Verify all tests pass**

## Benefits

1. ✅ **No reflection** - Direct method calls
2. ✅ **Type safety** - Compile-time checks
3. ✅ **IDE support** - Autocomplete, refactoring, navigation
4. ✅ **Better readability** - Clear method calls
5. ✅ **No PMD suppressions** - No AvoidAccessibilityAlteration needed
6. ✅ **Follows Jmix patterns** - Matches official documentation examples

## Risks & Mitigation

**Risk**: Class loader issues in Jmix test environment
**Mitigation**: Test casting first, fallback to reflection if needed

**Risk**: View casting may fail
**Mitigation**: Use instanceof check before cast, or try-catch

## Verification

After implementation:
1. All tests pass
2. No reflection warnings
3. Code compiles
4. PMD passes (no AvoidAccessibilityAlteration)
5. Documentation updated

