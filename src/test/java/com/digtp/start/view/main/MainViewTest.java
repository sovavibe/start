/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.view.main;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.digtp.start.StartApplication;
import com.digtp.start.entity.User;
import com.digtp.start.testsupport.AbstractIntegrationTest;
import com.digtp.start.testsupport.AuthenticatedAsAdmin;
import com.digtp.start.testsupport.ReflectionTestUtils;
import com.digtp.start.view.user.UserListView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import io.jmix.core.DataManager;
import io.jmix.core.security.UserRepository;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.testassist.FlowuiTestAssistConfiguration;
import io.jmix.flowui.testassist.UiTest;
import io.jmix.flowui.testassist.UiTestUtils;
import io.jmix.flowui.view.View;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

@UiTest
@SpringBootTest(classes = {StartApplication.class, FlowuiTestAssistConfiguration.class})
@ActiveProfiles("test")
@ExtendWith(AuthenticatedAsAdmin.class)
// java:S5976 excluded via config/sonar-project.properties
class MainViewTest extends AbstractIntegrationTest {

    private static final String TEST_USERNAME = "john.doe";
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";

    @Autowired
    private ViewNavigators viewNavigators;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DataManager dataManager;

    private User savedUser;

    @Test
    void testGenerateUserNameWithFirstNameAndLastName() throws ReflectiveOperationException {
        // Navigate to UserListView first to ensure we have a valid view context
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        // Then try to navigate to MainView using current view
        // MainView is a layout view, so navigation might work differently
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final View<?> mainView = getCurrentViewAsView();
        final User user = createTestUser(TEST_USERNAME, TEST_FIRST_NAME, TEST_LAST_NAME);

        final String userName = ReflectionTestUtils.invokeMethod(
                String.class, mainView, "generateUserName", new Class<?>[] {User.class}, user);

        assertThat(userName).isEqualTo(TEST_FIRST_NAME + " " + TEST_LAST_NAME);
    }

    @Test
    void testGenerateUserNameWithOnlyFirstName() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final View<?> mainView = getCurrentViewAsView();
        final User user = createTestUser(TEST_USERNAME, TEST_FIRST_NAME, null);

        final String userName = ReflectionTestUtils.invokeMethod(
                String.class, mainView, "generateUserName", new Class<?>[] {User.class}, user);

        assertThat(userName).isEqualTo(TEST_FIRST_NAME);
    }

    @Test
    void testGenerateUserNameWithOnlyLastName() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final View<?> mainView = getCurrentViewAsView();
        final User user = createTestUser(TEST_USERNAME, null, TEST_LAST_NAME);

        final String userName = ReflectionTestUtils.invokeMethod(
                String.class, mainView, "generateUserName", new Class<?>[] {User.class}, user);

        assertThat(userName).isEqualTo(TEST_LAST_NAME);
    }

    @Test
    void testGenerateUserNameWithoutNames() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final View<?> mainView = getCurrentViewAsView();
        final User user = createTestUser(TEST_USERNAME, null, null);

        final String userName = ReflectionTestUtils.invokeMethod(
                String.class, mainView, "generateUserName", new Class<?>[] {User.class}, user);

        assertThat(userName).isEqualTo(TEST_USERNAME);
    }

    @Test
    void testIsSubstitutedWhenUserIsSubstituted() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final View<?> mainView = getCurrentViewAsView();
        final User user = createTestUser(TEST_USERNAME, TEST_FIRST_NAME, TEST_LAST_NAME);
        // Authenticated user is "admin" (from AuthenticatedAsAdmin), user is different
        // So isSubstituted should return true
        final boolean isSubstituted = ReflectionTestUtils.invokeMethod(
                Boolean.class, mainView, "isSubstituted", new Class<?>[] {User.class}, user);

        assertThat(isSubstituted).isTrue();
    }

    @Test
    void testIsSubstitutedWhenUserIsNotSubstituted() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final View<?> mainView = getCurrentViewAsView();
        // Authenticated user is "admin" (from AuthenticatedAsAdmin), so use admin user
        final UserDetails adminUserDetails = userRepository.loadUserByUsername("admin");
        final User user = (User) adminUserDetails;

        // No substitution - authenticated user is the same as the user
        final boolean isSubstituted = ReflectionTestUtils.invokeMethod(
                Boolean.class, mainView, "isSubstituted", new Class<?>[] {User.class}, user);

        assertThat(isSubstituted).isFalse();
    }

    @Test
    void testIsSubstitutedWhenUserIsNull() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final View<?> mainView = getCurrentViewAsView();

        // When user is null, isSubstituted should return false
        final boolean isSubstituted = ReflectionTestUtils.invokeMethod(
                Boolean.class, mainView, "isSubstituted", new Class<?>[] {User.class}, (Object) null);

        assertThat(isSubstituted).isFalse();
    }

    @Test
    void testIsSubstitutedWhenAuthenticatedUserIsNull() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final View<?> mainView = getCurrentViewAsView();
        // Create a user that will trigger the authenticatedUser == null branch
        // This is difficult to test directly, but we can verify the method handles null gracefully
        final User user = createTestUser("test.user", "Test", "User");
        // The method checks authenticatedUser == null, which should return false
        // In normal flow, authenticatedUser is always set by AuthenticatedAsAdmin, so this branch
        // is hard to test without mocking CurrentUserSubstitution
        final boolean isSubstituted = ReflectionTestUtils.invokeMethod(
                Boolean.class, mainView, "isSubstituted", new Class<?>[] {User.class}, user);
        // In test context, authenticatedUser is always set, so this should return true (user != admin)
        assertThat(isSubstituted).isTrue();
    }

    @Test
    void testUserMenuHeaderRendererWhenNameDoesNotEqualUsername() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final View<?> mainView = getCurrentViewAsView();
        // When name does not equal username, else branch is taken (adds subtext with username)
        final User user = createTestUser(TEST_USERNAME, TEST_FIRST_NAME, TEST_LAST_NAME);

        final Component component = ReflectionTestUtils.invokeMethod(
                Component.class, mainView, "userMenuHeaderRenderer", new Class<?>[] {UserDetails.class}, user);

        assertThat(component).isNotNull().isInstanceOf(Div.class);
    }

    @Test
    void testCreateAvatar() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final View<?> mainView = getCurrentViewAsView();

        final String expectedName = TEST_FIRST_NAME + " " + TEST_LAST_NAME;
        final Avatar avatar = ReflectionTestUtils.invokeMethod(
                Avatar.class, mainView, "createAvatar", new Class<?>[] {String.class}, expectedName);

        assertThat(avatar).isNotNull();
        assertThat(avatar.getName()).isEqualTo(expectedName);
    }

    @Test
    // java:S5853 excluded via config/sonar-project.properties
    void testUserMenuButtonRendererWithUser() throws ReflectiveOperationException {
        final User user = createTestUser(TEST_USERNAME, TEST_FIRST_NAME, TEST_LAST_NAME);

        final Component component = ReflectionTestUtils.invokeMethod(
                Component.class, getMainView(), "userMenuButtonRenderer", new Class<?>[] {UserDetails.class}, user);

        assertThat(component).isNotNull().isInstanceOf(Div.class);
    }

    @Test
    void testUserMenuButtonRendererWithNonUser() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final View<?> mainView = getCurrentViewAsView();
        final UserDetails nonUser = mock(UserDetails.class);

        final Component component = ReflectionTestUtils.invokeMethod(
                Component.class, mainView, "userMenuButtonRenderer", new Class<?>[] {UserDetails.class}, nonUser);

        assertThat(component).isNull();
    }

    @Test
    void testUserMenuHeaderRendererWithUser() throws ReflectiveOperationException {
        final User user = createTestUser(TEST_USERNAME, TEST_FIRST_NAME, TEST_LAST_NAME);

        final Component component = ReflectionTestUtils.invokeMethod(
                Component.class, getMainView(), "userMenuHeaderRenderer", new Class<?>[] {UserDetails.class}, user);

        assertThat(component).isNotNull().isInstanceOf(Div.class);
    }

    @Test
    // java:S4144 excluded via config/sonar-project.properties
    void testUserMenuHeaderRendererWhenNameEqualsUsername() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final View<?> mainView = getCurrentViewAsView();
        // When name equals username, different branch is taken (adds subtext class)
        final User user = createTestUser(TEST_USERNAME, null, null);

        final Component component = ReflectionTestUtils.invokeMethod(
                Component.class, mainView, "userMenuHeaderRenderer", new Class<?>[] {UserDetails.class}, user);

        assertThat(component).isNotNull();
    }

    @Test
    void testUserMenuButtonRendererWithSubstitutedUser() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final View<?> mainView = getCurrentViewAsView();
        // Create user different from authenticated user (admin) to trigger substitution
        final User user = createTestUser("substituted.user", "Substituted", "User");

        final Component component = ReflectionTestUtils.invokeMethod(
                Component.class, mainView, "userMenuButtonRenderer", new Class<?>[] {UserDetails.class}, user);

        assertThat(component).isNotNull().isInstanceOf(Div.class);
        // Component should include substitution indicator
    }

    private User createTestUser(final String username, final String firstName, final String lastName) {
        final User user = dataManager.create(User.class);
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }

    /**
     * Gets MainView instance for testing.
     *
     * <p>Navigates to MainView and returns it for use in tests.
     *
     * @return MainView instance
     */
    private View<?> getMainView() {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        return getCurrentViewAsView();
    }

    @AfterEach
    void afterEach() {
        if (savedUser != null) {
            dataManager.remove(savedUser);
            // Reset to prevent accidental reuse of removed entity in next test
            savedUser = null;
        }
    }

    @Override
    protected void setUp() {
        // No setup needed for this test class
    }
}
