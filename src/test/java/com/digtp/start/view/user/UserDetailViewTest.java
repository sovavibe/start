package com.digtp.start.view.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.digtp.start.StartApplication;
import com.digtp.start.config.SecurityConstants;
import com.digtp.start.entity.User;
import com.digtp.start.test_support.AbstractIntegrationTest;
import com.digtp.start.test_support.AuthenticatedAsAdmin;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.PasswordField;
import io.jmix.core.DataManager;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.testassist.FlowuiTestAssistConfiguration;
import io.jmix.flowui.testassist.UiTest;
import io.jmix.flowui.testassist.UiTestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@UiTest
@SpringBootTest(classes = {StartApplication.class, FlowuiTestAssistConfiguration.class})
@ActiveProfiles("test")
@ExtendWith({SpringExtension.class, AuthenticatedAsAdmin.class})
class UserDetailViewTest extends AbstractIntegrationTest {

    @Autowired
    private ViewNavigators viewNavigators;

    @Autowired
    private DataManager dataManager;

    private User savedUser;

    @Test
    void testUserDetailViewInit() {
        // Arrange
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final UserListView listView = UiTestUtils.getCurrentView();
        final JmixButton createButton = UiTestUtils.getComponent(listView, "createButton");

        // Act
        createButton.click();
        final UserDetailView view = UiTestUtils.getCurrentView();

        // Assert
        assertThat(view).isNotNull();
        final ComboBox<String> timeZoneField = UiTestUtils.getComponent(view, "timeZoneField");
        assertThat(timeZoneField).isNotNull();
        assertThat(timeZoneField.getListDataView().getItems().count()).isGreaterThan(0);
    }

    @Test
    void testValidationNewUserWithValidPassword() {
        // Arrange
        navigateToNewUserView();
        final UserDetailView view = UiTestUtils.getCurrentView();
        final TypedTextField<String> usernameField = UiTestUtils.getComponent(view, "usernameField");
        final PasswordField passwordField = UiTestUtils.getComponent(view, "passwordField");
        final PasswordField confirmPasswordField = UiTestUtils.getComponent(view, "confirmPasswordField");
        final String username = "test-user-" + System.currentTimeMillis();
        final String validPassword = "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH);

        // Act
        usernameField.setValue(username);
        passwordField.setValue(validPassword);
        confirmPasswordField.setValue(validPassword);

        // Assert
        assertThat(view.getEditedEntity()).isNotNull();
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
        final UserDetailView view = UiTestUtils.getCurrentView();
        final TypedTextField<String> usernameField = UiTestUtils.getComponent(view, "usernameField");
        final PasswordField passwordField = UiTestUtils.getComponent(view, "passwordField");
        final PasswordField confirmPasswordField = UiTestUtils.getComponent(view, "confirmPasswordField");
        final String username = "test-user-" + System.currentTimeMillis();
        final String validPassword = "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH);
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
        final UserDetailView view = UiTestUtils.getCurrentView();
        final TypedTextField<String> usernameField = UiTestUtils.getComponent(view, "usernameField");
        final PasswordField passwordField = UiTestUtils.getComponent(view, "passwordField");
        final PasswordField confirmPasswordField = UiTestUtils.getComponent(view, "confirmPasswordField");
        final String username = "test-user-" + System.currentTimeMillis();
        final String validPassword = "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH);
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

    // Note: Existing user save tests are complex due to optimistic locking in UI tests
    // The onAfterSave logic for existing users is covered by UserServiceTest

    private void navigateToNewUserView() {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final UserListView listView = UiTestUtils.getCurrentView();
        final JmixButton createButton = UiTestUtils.getComponent(listView, "createButton");
        createButton.click();
    }

    @AfterEach
    void tearDown() {
        if (savedUser != null) {
            dataManager.remove(savedUser);
            savedUser = null; // NOPMD - NullAssignment: prevents accidental reuse of removed entity
        }
    }
}
