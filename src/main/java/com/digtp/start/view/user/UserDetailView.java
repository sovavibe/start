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
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    }

    @Subscribe
    public void onInitEntity(final InitEntityEvent<User> event) {
        usernameField.setReadOnly(false);
        passwordField.setVisible(true);
        confirmPasswordField.setVisible(true);
    }

    @Subscribe
    public void onReady(final ReadyEvent event) {
        if (entityStates.isNew(getEditedEntity())) {
            usernameField.focus();
        }
    }

    @Subscribe
    public void onValidation(final ValidationEvent event) {
        if (entityStates.isNew(getEditedEntity())
                && !userService.validatePasswordConfirmation(
                        passwordField.getValue(), confirmPasswordField.getValue())) {
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
        } else {
            userService.prepareUserForSave(user, null, false);
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
