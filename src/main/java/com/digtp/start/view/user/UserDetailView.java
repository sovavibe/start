package com.digtp.start.view.user;

import com.digtp.start.entity.User;
import com.digtp.start.service.UserService;
import com.digtp.start.view.main.MainView;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;
import io.jmix.core.EntityStates;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.MessageBundle;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Detail view for User entity creation and editing.
 *
 * <p>Provides form-based editing of user properties including username, password,
 * email, timezone, and active status. Validates password strength and confirmation
 * for new users and password changes.
 */
@Route(value = "users/:id", layout = MainView.class)
@ViewController(id = "User.detail")
@ViewDescriptor(path = "user-detail-view.xml")
@EditedEntityContainer("userDc")
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings({"java:S1948", "java:S110", "java:S2177"
}) // Framework patterns: Vaadin views. Required for Gradle SonarLint plugin.
public class UserDetailView extends StandardDetailView<User> {

    @ViewComponent
    private TypedTextField<String> usernameField;

    @ViewComponent
    private PasswordField passwordField;

    @ViewComponent
    private PasswordField confirmPasswordField;

    @ViewComponent
    private ComboBox<String> timeZoneField;

    @ViewComponent
    private MessageBundle messageBundle;

    private final transient Notifications notifications;
    private final transient EntityStates entityStates;
    private final transient UserService userService;

    private boolean newEntity;

    @Subscribe
    public void onInit(final InitEvent event) {
        timeZoneField.setItems(List.of(TimeZone.getAvailableIDs()));
        log.debug("User detail view initialized");
    }

    @Subscribe
    public void onInitEntity(final InitEntityEvent<User> event) {
        final User user = event.getEntity();
        usernameField.setReadOnly(false);
        passwordField.setVisible(true);
        confirmPasswordField.setVisible(true);
        log.debug(
                "User entity initialized: isNew={}, username={}",
                entityStates.isNew(user),
                user.getUsername() != null ? user.getUsername() : "not set");
    }

    @Subscribe
    public void onReady(final ReadyEvent event) {
        final User user = getEditedEntity();
        if (entityStates.isNew(user)) {
            usernameField.focus();
            log.debug("New user detail view ready: username field focused");
        } else {
            log.debug("User detail view ready: id={}, username={}", user.getId(), user.getUsername());
        }
    }

    @Subscribe
    public void onValidation(final ValidationEvent event) {
        final User user = getEditedEntity();
        final boolean isNew = entityStates.isNew(user);
        final String password = passwordField.getValue();
        final String confirmPassword = confirmPasswordField.getValue();

        if (isNew) {
            validateNewUserPassword(event, password, confirmPassword);
        } else if (password != null && !password.isEmpty()) {
            validatePasswordChange(event, password, confirmPassword);
        }

        if (event.getErrors().isEmpty()) {
            log.debug(
                    "User validation passed: id={}, username={}",
                    Objects.toString(user.getId(), "new"),
                    user.getUsername());
        }
    }

    private void validateNewUserPassword(
            final ValidationEvent event, final String password, final String confirmPassword) {
        if (password == null || password.isEmpty()) {
            log.warn("Password validation failed: password is required for new user");
            event.getErrors().add(messageBundle.getMessage("passwordRequired"));
            return;
        }

        validatePasswordStrength(event, password);
        validatePasswordConfirmation(event, password, confirmPassword);
    }

    private void validatePasswordChange(
            final ValidationEvent event, final String password, final String confirmPassword) {
        validatePasswordStrength(event, password);
        validatePasswordConfirmation(event, password, confirmPassword);
    }

    /**
     * Validates password strength requirements.
     *
     * <p>Delegates to UserService for password strength validation.
     * Adds validation errors to the event if validation fails.
     *
     * @param event validation event to add errors to
     * @param password password to validate
     */
    private void validatePasswordStrength(final ValidationEvent event, final String password) {
        try {
            userService.validatePasswordStrength(password);
        } catch (final IllegalArgumentException e) {
            log.warn("Password validation failed: {}", e.getMessage());
            event.getErrors().add(e.getMessage());
        }
    }

    /**
     * Validates that password and confirmation password match.
     *
     * <p>Delegates to UserService for password confirmation validation.
     * Adds validation errors to the event if passwords do not match.
     *
     * @param event validation event to add errors to
     * @param password password value
     * @param confirmPassword confirmation password value
     */
    private void validatePasswordConfirmation(
            final ValidationEvent event, final String password, final String confirmPassword) {
        if (!userService.validatePasswordConfirmation(password, confirmPassword)) {
            log.warn("Password validation failed: passwords do not match");
            event.getErrors().add(messageBundle.getMessage("passwordsDoNotMatch"));
        }
    }

    @Subscribe
    public void onBeforeSave(final BeforeSaveEvent event) {
        final User user = getEditedEntity();
        final boolean isNew = entityStates.isNew(user);
        if (isNew) {
            userService.prepareUserForSave(user, passwordField.getValue(), true);
            newEntity = true;
            log.debug("Preparing new user for save: username={}", user.getUsername());
        } else {
            final String newPassword = passwordField.getValue();
            userService.prepareUserForSave(user, newPassword, false);
            log.debug(
                    "Preparing user update for save: id={}, username={}, passwordChanged={}",
                    user.getId(),
                    user.getUsername(),
                    newPassword != null && !newPassword.isEmpty());
        }
    }

    @Subscribe
    public void onAfterSave(final AfterSaveEvent event) {
        final User user = getEditedEntity();
        if (newEntity) {
            log.info("User created successfully: id={}, username={}", user.getId(), user.getUsername());
            notifications
                    .create(messageBundle.getMessage("noAssignedRolesNotification"))
                    .withThemeVariant(NotificationVariant.LUMO_WARNING)
                    .withPosition(Notification.Position.TOP_END)
                    .show();
            newEntity = false;
        } else {
            log.info("User updated successfully: id={}, username={}", user.getId(), user.getUsername());
        }
    }
}
