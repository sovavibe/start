package com.digtp.start.view.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.digtp.start.StartApplication;
import com.digtp.start.config.SecurityConstants;
import com.digtp.start.entity.User;
import com.digtp.start.test_support.AbstractIntegrationTest;
import com.digtp.start.test_support.AuthenticatedAsAdmin;
import com.vaadin.flow.component.textfield.PasswordField;
import io.jmix.core.DataManager;
import io.jmix.flowui.ViewNavigators;
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

/**
 * E2E integration tests for UserDetailView editing existing users.
 *
 * <p>Tests full end-to-end scenarios for editing existing user entities,
 * covering onBeforeSave and onAfterSave else branches for existing users.
 * This test class focuses on complete user workflows including navigation,
 * editing, and saving existing entities.
 */
@UiTest
@SpringBootTest(classes = {StartApplication.class, FlowuiTestAssistConfiguration.class})
@ActiveProfiles("test")
@ExtendWith({SpringExtension.class, AuthenticatedAsAdmin.class})
class UserDetailViewE2ETest extends AbstractIntegrationTest {

    @Autowired
    private ViewNavigators viewNavigators;

    @Autowired
    private DataManager dataManager;

    private User savedUser;

    /**
     * E2E test for editing existing user with password change.
     *
     * <p>Tests complete workflow: create, navigate, edit, save, verify.
     * Covers onReady, onBeforeSave, onAfterSave else branches for existing users.
     */
    @Test
    void testEditExistingUserWithPasswordChange() {
        // Arrange
        savedUser = createAndReloadUser();
        navigateToExistingUserView(savedUser);
        final UserDetailView view = UiTestUtils.getCurrentView();
        assertThat(view.getEditedEntity()).isNotNull();
        assertThat(view.getEditedEntity().getId()).isEqualTo(savedUser.getId());

        // Act
        final String newPassword = changePasswordAndSave(view);

        // Assert
        verifyUserSaved(savedUser, newPassword);
    }

    private User createAndReloadUser() {
        final User user = dataManager.create(User.class);
        user.setUsername("test-user-" + System.currentTimeMillis());
        user.setPassword("encoded-password");
        final User createdUser = dataManager.save(user);
        return dataManager.load(User.class).id(createdUser.getId()).one();
    }

    private void navigateToExistingUserView(final User user) {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        viewNavigators
                .detailView(UiTestUtils.getCurrentView(), User.class)
                .editEntity(user)
                .navigate();
    }

    private String changePasswordAndSave(final UserDetailView view) {
        final PasswordField passwordField = UiTestUtils.getComponent(view, "passwordField");
        final PasswordField confirmPasswordField = UiTestUtils.getComponent(view, "confirmPasswordField");
        final String newPassword = "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH);
        passwordField.setValue(newPassword);
        confirmPasswordField.setValue(newPassword);
        final JmixButton saveButton = UiTestUtils.getComponent(view, "saveAndCloseButton");
        saveButton.click();
        return newPassword;
    }

    private void verifyUserSaved(final User originalUser, final String newPassword) {
        final User loadedUser =
                dataManager.load(User.class).id(originalUser.getId()).one();
        assertThat(loadedUser).isNotNull();
        assertThat(loadedUser.getPassword()).isNotBlank();
        assertThat(loadedUser.getPassword()).isNotEqualTo(newPassword);
        assertThat(loadedUser.getVersion()).isGreaterThan(originalUser.getVersion());
        savedUser = loadedUser;
    }

    @AfterEach
    void tearDown() {
        if (savedUser != null) {
            final User userToRemove =
                    dataManager.load(User.class).id(savedUser.getId()).one();
            if (userToRemove != null) {
                dataManager.remove(userToRemove);
            }
            savedUser = null; // NOPMD - NullAssignment: prevents accidental reuse
        }
    }
}
