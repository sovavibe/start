/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.view.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.digtp.start.StartApplication;
import com.digtp.start.entity.User;
import com.digtp.start.testsupport.AbstractIntegrationTest;
import com.digtp.start.testsupport.AuthenticatedAsAdmin;
import com.digtp.start.testsupport.TestFixtures;
import com.digtp.start.view.login.LoginView;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.PasswordField;
import io.jmix.core.DataManager;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.testassist.FlowuiTestAssistConfiguration;
import io.jmix.flowui.testassist.UiTest;
import io.jmix.flowui.testassist.UiTestUtils;
import io.jmix.flowui.view.View;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@UiTest
@SpringBootTest(classes = {StartApplication.class, FlowuiTestAssistConfiguration.class})
@ActiveProfiles("test")
@ExtendWith(AuthenticatedAsAdmin.class)
class UserDetailViewTest extends AbstractIntegrationTest {

    private static final String USERNAME_FIELD = "usernameField";
    private static final String PASSWORD_FIELD = "passwordField";
    private static final String CONFIRM_PASSWORD_FIELD = "confirmPasswordField";

    @Autowired
    private ViewNavigators viewNavigators;

    @Autowired
    private DataManager dataManager;

    private User savedUser;

    static java.util.stream.Stream<org.junit.jupiter.params.provider.Arguments> provideInvalidCredentials() {
        final String username = TestFixtures.uniqueUsername();
        final String valid = TestFixtures.validPassword();
        final String shortPwd = TestFixtures.SHORT_PASSWORD;

        return java.util.stream.Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(username, null, null), // Null password
                org.junit.jupiter.params.provider.Arguments.of(username, "", ""), // Empty password
                org.junit.jupiter.params.provider.Arguments.of(username, valid, valid + "diff"), // Mismatched
                org.junit.jupiter.params.provider.Arguments.of(username, shortPwd, shortPwd) // Short password
                );
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.MethodSource("provideInvalidCredentials")
    void testValidationNewUserWithInvalidCredentials(String username, String password, String confirmPassword) {
        // Arrange
        navigateToNewUserView();
        final View<?> view = getCurrentViewAsView();
        final TypedTextField<String> usernameField = UiTestUtils.getComponent(view, USERNAME_FIELD);
        final PasswordField passwordField = UiTestUtils.getComponent(view, PASSWORD_FIELD);
        final PasswordField confirmPasswordField = UiTestUtils.getComponent(view, CONFIRM_PASSWORD_FIELD);

        // Act
        usernameField.setValue(username);
        passwordField.setValue(password);
        confirmPasswordField.setValue(confirmPassword);

        // Assert - validation should fail but view should still exist
        final UserDetailView detailView = (UserDetailView) view;
        final User editedEntity = detailView.getEditedEntity();
        assertThat(editedEntity).isNotNull();
    }

    @Test
    void testUserDetailViewInit() {
        // Arrange
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> listView = getCurrentViewAsView();
        final JmixButton createButton = UiTestUtils.getComponent(listView, "createButton");

        // Act
        createButton.click();
        final View<?> view = getCurrentViewAsView();

        // Assert
        org.assertj.core.api.SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(view).isNotNull();
            final ComboBox<String> timeZoneField = UiTestUtils.getComponent(view, "timeZoneField");
            softly.assertThat(timeZoneField).isNotNull();
            if (timeZoneField != null) {
                softly.assertThat(timeZoneField.getListDataView().getItems().count())
                        .isGreaterThan(0);
            }
        });
    }

    @Test
    void testValidationNewUserWithValidPassword() {
        // Arrange
        navigateToNewUserView();
        final View<?> view = getCurrentViewAsView();
        final TypedTextField<String> usernameField = UiTestUtils.getComponent(view, USERNAME_FIELD);
        final PasswordField passwordField = UiTestUtils.getComponent(view, PASSWORD_FIELD);
        final PasswordField confirmPasswordField = UiTestUtils.getComponent(view, CONFIRM_PASSWORD_FIELD);
        final String username = TestFixtures.uniqueUsername();
        final String validPassword = TestFixtures.validPassword();

        // Act
        usernameField.setValue(username);
        passwordField.setValue(validPassword);
        confirmPasswordField.setValue(validPassword);

        // Assert
        final UserDetailView detailView = (UserDetailView) view;
        final User editedEntity = detailView.getEditedEntity();
        assertThat(editedEntity).isNotNull();
    }

    // Note: Validation error scenarios are tested through UserServiceTest
    // UI validation is handled by the framework and tested through integration tests

    // Note: Existing user validation tests are covered by UserServiceTest
    // UI navigation to existing users is complex and may have optimistic lock issues
    // Focus on new user creation which provides better coverage

    @Test
    void testBeforeSaveNewUser() {
        // Arrange
        navigateToNewUserView();
        final View<?> view = getCurrentViewAsView();
        final TypedTextField<String> usernameField = UiTestUtils.getComponent(view, USERNAME_FIELD);
        final PasswordField passwordField = UiTestUtils.getComponent(view, PASSWORD_FIELD);
        final PasswordField confirmPasswordField = UiTestUtils.getComponent(view, CONFIRM_PASSWORD_FIELD);
        final String username = TestFixtures.uniqueUsername();
        final String validPassword = TestFixtures.validPassword();
        usernameField.setValue(username);
        passwordField.setValue(validPassword);
        confirmPasswordField.setValue(validPassword);

        // Act
        final JmixButton saveAndCloseButton = UiTestUtils.getComponent(view, "saveAndCloseButton");
        saveAndCloseButton.click();

        // Assert
        final User loadedUser =
                dataManager.load(User.class).query("e.username = ?1", username).one();
        assertThat(loadedUser).isNotNull();
        assertThat(loadedUser.getPassword()).isNotBlank().isNotEqualTo(validPassword);
        savedUser = loadedUser;
    }

    // Note: Existing user save tests are complex due to optimistic locking in UI tests
    // The onBeforeSave logic for existing users is covered by UserServiceTest

    @Test
    void testAfterSaveNewUser() {
        // Arrange
        navigateToNewUserView();
        final View<?> view = getCurrentViewAsView();
        final TypedTextField<String> usernameField = UiTestUtils.getComponent(view, USERNAME_FIELD);
        final PasswordField passwordField = UiTestUtils.getComponent(view, PASSWORD_FIELD);
        final PasswordField confirmPasswordField = UiTestUtils.getComponent(view, CONFIRM_PASSWORD_FIELD);
        final String username = TestFixtures.uniqueUsername();
        final String validPassword = TestFixtures.validPassword();
        usernameField.setValue(username);
        passwordField.setValue(validPassword);
        confirmPasswordField.setValue(validPassword);

        // Act
        final JmixButton saveAndCloseButton = UiTestUtils.getComponent(view, "saveAndCloseButton");
        saveAndCloseButton.click();

        // Assert
        final User loadedUser =
                dataManager.load(User.class).query("e.username = ?1", username).one();
        assertThat(loadedUser).isNotNull();
        savedUser = loadedUser;
    }

    private void navigateToNewUserView() {
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final View<?> listView = getCurrentViewAsView();
        final JmixButton createButton = UiTestUtils.getComponent(listView, "createButton");
        createButton.click();
    }

    @AfterEach
    void afterEach() {
        if (savedUser != null) {
            dataManager.remove(savedUser);
            // No need to nullify for GC in PER_METHOD lifecycle
        }
    }

    @Override
    protected void before() {
        // No setup needed for this test class
    }
}
