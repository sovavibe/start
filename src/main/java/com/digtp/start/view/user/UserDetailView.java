/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.view.user;

import com.digtp.start.config.SecurityConstants;
import com.digtp.start.entity.User;
import com.digtp.start.service.AuditService;
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
// Framework: Jmix views extend multiple framework classes (StandardDetailView, etc.)
@SuppressWarnings({
    // Framework: Jmix views contain framework-managed non-serializable beans (MessageBundle, UI components)
    "java:S1948", // non-serializable field
    // Framework: Jmix views extend multiple framework classes (StandardDetailView, etc.)
    "java:S110", // too many parents
    // Framework: Jmix lifecycle methods may have same names as parent methods
    "java:S2177", // method name conflict
    // Framework: Jmix views extend StandardDetailView which requires design for extension
    "java:S2150", // design for extension
    // Framework: Jmix lifecycle methods (onInit, etc.) don't need JavaDoc
    "java:S1186", // missing javadoc
    // Framework: @ViewComponent is Vaadin/Jmix mechanism for UI component injection from XML (not Spring field
    // injection)
    "java:S6813", // field injection
    // Framework: Error Prone StrictUnusedVariable requires underscore prefix for unused variables
    "java:S117", // unused variable
    // Framework: Jmix View contains framework-managed non-serializable beans (MessageBundle, UI components)
    "PMD.NonSerializableClass",
    // Framework: Jmix View: @ViewComponent fields must be after constructor-injected fields
    "PMD.FieldDeclarationsShouldBeAtStartOfClass"
})
public class UserDetailView extends StandardDetailView<User> {

    private static final long serialVersionUID = 1L;

    private final transient Notifications notifications;
    private final transient EntityStates entityStates;
    private final transient UserService userService;
    private final transient AuditService auditService;

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

    /**
     * Tracks whether entity was new when saved.
     *
     * <p>Used to determine if notification should be shown after save.
     * Set in onBeforeSave, checked in onAfterSave, then reset.
     * Transient to avoid serialization issues.
     */
    private transient boolean wasNewOnSave;

    /**
     * Initializes the view.
     *
     * <p>This method is safe to override. Override to customize view initialization.
     *
     * @param _event initialization event (unused, required by framework)
     */
    @Subscribe
    public void onInit(final InitEvent _event) {
        timeZoneField.setItems(getAvailableTimeZoneIds());
        log.debug("User detail view initialized");
    }

    /**
     * Returns cached list of available timezone IDs.
     *
     * <p>TimeZone IDs are static and don't change, so we cache them to avoid
     * repeated calls to TimeZone.getAvailableIDs() which creates a new array each time.
     *
     * @return immutable list of timezone IDs
     */
    private static List<String> getAvailableTimeZoneIds() {
        return AVAILABLE_TIME_ZONE_IDS;
    }

    /**
     * Cached list of available timezone IDs.
     *
     * <p>Initialized once at class load time to avoid repeated calls to
     * TimeZone.getAvailableIDs() which is expensive and creates new arrays.
     */
    private static final List<String> AVAILABLE_TIME_ZONE_IDS = List.of(TimeZone.getAvailableIDs());

    /**
     * Initializes entity when view is opened.
     *
     * <p>This method is safe to override. Override to customize entity initialization.
     *
     * @param event entity initialization event
     */
    @Subscribe
    public void onInitEntity(final InitEntityEvent<User> event) {
        final User user = event.getEntity();
        usernameField.setReadOnly(false);
        passwordField.setVisible(true);
        confirmPasswordField.setVisible(true);
        log.debug(
                "User entity initialized: isNew={}, username={}",
                entityStates.isNew(user),
                Objects.toString(user.getUsername(), "not set"));
    }

    /**
     * Called when view is ready.
     *
     * <p>This method is safe to override. Override to customize view ready behavior.
     *
     * @param _event ready event (unused, required by framework)
     */
    @Subscribe
    public void onReady(final ReadyEvent _event) {
        final User user = getEditedEntity();
        if (entityStates.isNew(user)) {
            usernameField.focus();
            log.debug("New user detail view ready: username field focused");
        } else {
            log.debug("User detail view ready: id={}, username={}", user.getId(), user.getUsername());
        }
    }

    /**
     * Validates entity before save.
     *
     * <p>This method is safe to override. Override to customize validation logic.
     *
     * @param event validation event
     */
    @Subscribe
    public void onValidation(final ValidationEvent event) {
        final User user = getEditedEntity();
        final boolean isNew = entityStates.isNew(user);
        final String password = passwordField.getValue();
        final String confirmPassword = confirmPasswordField.getValue();

        if (isNew) {
            validatePasswordForNewUser(event, password, confirmPassword);
        } else if (SecurityConstants.isNotNullOrEmpty(password)) {
            validatePasswordChange(event, password, confirmPassword);
        }

        if (event.getErrors().isEmpty()) {
            log.debug(
                    "User validation passed: id={}, username={}",
                    Objects.toString(user.getId(), "new"),
                    user.getUsername());
        }
    }

    private void validatePasswordForNewUser(
            final ValidationEvent event, final String password, final String confirmPassword) {
        if (SecurityConstants.isNullOrEmpty(password)) {
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
     * @param event    validation event to add errors to
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
     * @param event           validation event to add errors to
     * @param password        password value
     * @param confirmPassword confirmation password value
     */
    private void validatePasswordConfirmation(
            final ValidationEvent event, final String password, final String confirmPassword) {
        if (!userService.validatePasswordConfirmation(password, confirmPassword)) {
            log.warn("Password validation failed: passwords do not match");
            event.getErrors().add(messageBundle.getMessage("passwordsDoNotMatch"));
        }
    }

    /**
     * Called before entity is saved.
     *
     * <p>This method is safe to override. Override to customize pre-save logic.
     *
     * @param _event before save event (unused, required by framework)
     */
    @Subscribe
    public void onBeforeSave(final BeforeSaveEvent _event) {
        final User user = getEditedEntity();
        final boolean isNew = entityStates.isNew(user);
        wasNewOnSave = isNew; // Store state for use in onAfterSave
        try {
            if (isNew) {
                userService.prepareUserForSave(user, passwordField.getValue(), true);
                log.debug("Preparing new user for save: username={}", user.getUsername());
            } else {
                final String newPassword = passwordField.getValue();
                userService.prepareUserForSave(user, newPassword, false);
                log.debug(
                        "Preparing user update: id={}, username={}, passwordChanged={}",
                        user.getId(),
                        user.getUsername(),
                        SecurityConstants.isNotNullOrEmpty(newPassword));
            }
        } catch (final Exception exception) {
            log.error(
                    "Failed to prepare user for save: id={}, username={}, isNew={}, error={}",
                    user.getId(),
                    user.getUsername(),
                    isNew,
                    exception.getMessage(),
                    exception);
            throw exception; // Re-throw to let framework handle it
        }
    }

    /**
     * Called after entity is saved.
     *
     * <p>This method is safe to override. Override to customize post-save logic.
     *
     * @param _event after save event (unused, required by framework)
     */
    @Subscribe
    public void onAfterSave(final AfterSaveEvent _event) {
        final User user = getEditedEntity();
        if (wasNewOnSave) {
            log.info("User created successfully: id={}, username={}", user.getId(), user.getUsername());
            auditService.logUserCreated(user.getId(), user.getUsername());
            notifications
                    .create(messageBundle.getMessage("noAssignedRolesNotification"))
                    .withThemeVariant(NotificationVariant.LUMO_WARNING)
                    .withPosition(Notification.Position.TOP_END)
                    .show();
            wasNewOnSave = false; // Reset after use
        } else {
            final String newPassword = passwordField.getValue();
            final boolean passwordChanged = SecurityConstants.isNotNullOrEmpty(newPassword);
            log.info(
                    "User updated successfully: id={}, username={}, passwordChanged={}",
                    user.getId(),
                    user.getUsername(),
                    passwordChanged);
            auditService.logUserUpdated(user.getId(), user.getUsername());
            if (passwordChanged) {
                auditService.logPasswordChanged(user.getId(), user.getUsername());
            }
        }
    }
}
