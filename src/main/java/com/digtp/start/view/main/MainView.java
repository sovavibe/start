package com.digtp.start.view.main;

import com.digtp.start.entity.User;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import io.jmix.core.Messages;
import io.jmix.core.usersubstitution.CurrentUserSubstitution;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.app.main.StandardMainView;
import io.jmix.flowui.view.Install;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@Route("")
@ViewController(id = "MainView")
@ViewDescriptor(path = "main-view.xml")
@RequiredArgsConstructor
@SuppressWarnings(
        "java:S1948") // Framework pattern: Vaadin views contain non-serializable deps. Required for Gradle SonarLint
// plugin.
public class MainView extends StandardMainView {

    private final Messages messages;
    private final UiComponents uiComponents;
    private final CurrentUserSubstitution currentUserSubstitution;

    @Install(to = "userMenu", subject = "buttonRenderer")
    private Component userMenuButtonRenderer(final UserDetails userDetails) {
        if (!(userDetails instanceof User user)) {
            return null;
        }

        final String userName = generateUserName(user);

        final Div content = uiComponents.create(Div.class);
        content.setClassName("user-menu-button-content");

        final Avatar avatar = createAvatar(userName);

        final Span name = uiComponents.create(Span.class);
        name.setText(userName);
        name.setClassName("user-menu-text");

        content.add(avatar, name);

        if (isSubstituted(user)) {
            final Span subtext = uiComponents.create(Span.class);
            subtext.setText(messages.getMessage("userMenu.substituted"));
            subtext.setClassName("user-menu-subtext");

            content.add(subtext);
        }

        return content;
    }

    @Install(to = "userMenu", subject = "headerRenderer")
    private Component userMenuHeaderRenderer(final UserDetails userDetails) {
        if (!(userDetails instanceof User user)) {
            return null;
        }

        final Div content = uiComponents.create(Div.class);
        content.setClassName("user-menu-header-content");

        final String name = generateUserName(user);

        final Avatar avatar = createAvatar(name);
        avatar.addThemeVariants(AvatarVariant.LUMO_LARGE);

        final Span text = uiComponents.create(Span.class);
        text.setText(name);
        text.setClassName("user-menu-text");

        content.add(avatar, text);

        if (name.equals(user.getUsername())) {
            text.addClassNames("user-menu-text-subtext");
        } else {
            final Span subtext = uiComponents.create(Span.class);
            subtext.setText(user.getUsername());
            subtext.setClassName("user-menu-subtext");

            content.add(subtext);
        }

        return content;
    }

    private Avatar createAvatar(final String fullName) {
        final Avatar avatar = uiComponents.create(Avatar.class);
        avatar.setName(fullName);
        avatar.getElement().setAttribute("tabindex", "-1");
        avatar.setClassName("user-menu-avatar");

        return avatar;
    }

    private String generateUserName(final User user) {
        final String firstName = Objects.requireNonNullElse(user.getFirstName(), "");
        final String lastName = Objects.requireNonNullElse(user.getLastName(), "");
        final String userName = String.format("%s %s", firstName, lastName).trim();

        return userName.isEmpty() ? user.getUsername() : userName;
    }

    private boolean isSubstituted(final User user) {
        final UserDetails authenticatedUser = currentUserSubstitution.getAuthenticatedUser();
        return user != null && !authenticatedUser.getUsername().equals(user.getUsername());
    }
}
