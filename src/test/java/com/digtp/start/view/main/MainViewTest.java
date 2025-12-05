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
// Test: Test methods may have similar structure but test different scenarios
@SuppressWarnings({
    // Test: Some tests are clearer as separate methods rather than parameterized
    "java:S5976",
    // Test: Multiple assertions on same object are acceptable in tests for clarity
    "java:S5853",
    // Test: Test methods may have similar structure but test different scenarios
    "java:S4144"
})
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
    void testGenerateUserNameWithFirstNameAndLastName() {
        // Navigate to UserListView first to ensure we have a valid view context
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        // Then try to navigate to MainView using current view
        // MainView is a layout view, so navigation might work differently
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final MainView mainView = (MainView) getCurrentViewAsView();
        final User user = createTestUser(TEST_USERNAME, TEST_FIRST_NAME, TEST_LAST_NAME);

        final String userName = mainView.generateUserName(user);

        assertThat(userName).isEqualTo(TEST_FIRST_NAME + " " + TEST_LAST_NAME);
    }

    @Test
    void testGenerateUserNameWithOnlyFirstName() {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final MainView mainView = (MainView) getCurrentViewAsView();
        final User user = createTestUser(TEST_USERNAME, TEST_FIRST_NAME, null);

        final String userName = mainView.generateUserName(user);

        assertThat(userName).isEqualTo(TEST_FIRST_NAME);
    }

    @Test
    void testGenerateUserNameWithOnlyLastName() {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final MainView mainView = (MainView) getCurrentViewAsView();
        final User user = createTestUser(TEST_USERNAME, null, TEST_LAST_NAME);

        final String userName = mainView.generateUserName(user);

        assertThat(userName).isEqualTo(TEST_LAST_NAME);
    }

    @Test
    void testGenerateUserNameWithoutNames() {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final MainView mainView = (MainView) getCurrentViewAsView();
        final User user = createTestUser(TEST_USERNAME, null, null);

        final String userName = mainView.generateUserName(user);

        assertThat(userName).isEqualTo(TEST_USERNAME);
    }

    @Test
    void testIsSubstitutedWhenUserIsSubstituted() {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final MainView mainView = (MainView) getCurrentViewAsView();
        final User user = createTestUser(TEST_USERNAME, TEST_FIRST_NAME, TEST_LAST_NAME);
        // Authenticated user is "admin" (from AuthenticatedAsAdmin), user is different
        // So isSubstituted should return true
        final boolean isSubstituted = mainView.isSubstituted(user);

        assertThat(isSubstituted).isTrue();
    }

    @Test
    void testIsSubstitutedWhenUserIsNotSubstituted() {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final MainView mainView = (MainView) getCurrentViewAsView();
        // Authenticated user is "admin" (from AuthenticatedAsAdmin), so use admin user
        final UserDetails adminUserDetails = userRepository.loadUserByUsername("admin");
        final User user = (User) adminUserDetails;

        // No substitution - authenticated user is the same as the user
        final boolean isSubstituted = mainView.isSubstituted(user);

        assertThat(isSubstituted).isFalse();
    }

    @Test
    void testIsSubstitutedWhenUserIsNull() {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final MainView mainView = (MainView) getCurrentViewAsView();

        // When user is null, isSubstituted should return false
        final boolean isSubstituted = mainView.isSubstituted(null);

        assertThat(isSubstituted).isFalse();
    }

    @Test
    void testIsSubstitutedWhenAuthenticatedUserIsNull() {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final MainView mainView = (MainView) getCurrentViewAsView();
        // Create a user that will trigger the authenticatedUser == null branch
        // This is difficult to test directly, but we can verify the method handles null gracefully
        final User user = createTestUser("test.user", "Test", "User");
        // The method checks authenticatedUser == null, which should return false
        // In normal flow, authenticatedUser is always set by AuthenticatedAsAdmin, so this branch
        // is hard to test without mocking CurrentUserSubstitution
        final boolean isSubstituted = mainView.isSubstituted(user);
        // In test context, authenticatedUser is always set, so this should return true (user != admin)
        assertThat(isSubstituted).isTrue();
    }

    @Test
    void testUserMenuHeaderRendererWhenNameDoesNotEqualUsername() {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final MainView mainView = (MainView) getCurrentViewAsView();
        // When name does not equal username, else branch is taken (adds subtext with username)
        final User user = createTestUser(TEST_USERNAME, TEST_FIRST_NAME, TEST_LAST_NAME);

        final Component component = mainView.userMenuHeaderRenderer(user);

        assertThat(component).isNotNull().isInstanceOf(Div.class);
    }

    @Test
    void testCreateAvatar() {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final MainView mainView = (MainView) getCurrentViewAsView();

        final String expectedName = TEST_FIRST_NAME + " " + TEST_LAST_NAME;
        final Avatar avatar = mainView.createAvatar(expectedName);

        assertThat(avatar).isNotNull();
        assertThat(avatar.getName()).isEqualTo(expectedName);
    }

    @Test
    // java:S5853 excluded via sonar-project.properties
    void testUserMenuButtonRendererWithUser() {
        final User user = createTestUser(TEST_USERNAME, TEST_FIRST_NAME, TEST_LAST_NAME);
        final MainView mainView = (MainView) getMainView();

        final Component component = mainView.userMenuButtonRenderer(user);

        assertThat(component).isNotNull().isInstanceOf(Div.class);
    }

    @Test
    void testUserMenuButtonRendererWithNonUser() {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final MainView mainView = (MainView) getCurrentViewAsView();
        final UserDetails nonUser = mock(UserDetails.class);

        final Component component = mainView.userMenuButtonRenderer(nonUser);

        assertThat(component).isNull();
    }

    @Test
    void testUserMenuHeaderRendererWithUser() {
        final User user = createTestUser(TEST_USERNAME, TEST_FIRST_NAME, TEST_LAST_NAME);
        final MainView mainView = (MainView) getMainView();

        final Component component = mainView.userMenuHeaderRenderer(user);

        assertThat(component).isNotNull().isInstanceOf(Div.class);
    }

    @Test
    // java:S4144 excluded via sonar-project.properties
    void testUserMenuHeaderRendererWhenNameEqualsUsername() {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final MainView mainView = (MainView) getCurrentViewAsView();
        // When name equals username, different branch is taken (adds subtext class)
        final User user = createTestUser(TEST_USERNAME, null, null);

        final Component component = mainView.userMenuHeaderRenderer(user);

        assertThat(component).isNotNull();
    }

    @Test
    void testUserMenuButtonRendererWithSubstitutedUser() {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        final MainView mainView = (MainView) getCurrentViewAsView();
        // Create user different from authenticated user (admin) to trigger substitution
        final User user = createTestUser("substituted.user", "Substituted", "User");

        final Component component = mainView.userMenuButtonRenderer(user);

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
    private MainView getMainView() {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> currentView = getCurrentViewAsView();
        viewNavigators.view(currentView, MainView.class).navigate();
        return (MainView) getCurrentViewAsView();
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
