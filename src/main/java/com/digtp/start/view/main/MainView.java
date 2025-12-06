/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.view.main;

import com.digtp.start.entity.User;
import com.google.common.annotations.VisibleForTesting;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Main application view.
 *
 * <p>Provides the main application layout with navigation menu and user menu.
 * Displays user information and handles user substitution indicators.
 */
@Route("")
@ViewController(id = "MainView")
@ViewDescriptor(path = "main-view.xml")
@RequiredArgsConstructor
@Slf4j
public class MainView extends StandardMainView {

    private static final long serialVersionUID = 1L;

    /**
     * Tabindex value to exclude element from keyboard navigation.
     *
     * <p>Standard HTML/ARIA value for non-interactive decorative elements.
     */
    private static final String TABINDEX_EXCLUDED = "-1";

    private final transient Messages messages;
    private final transient UiComponents uiComponents;
    private final transient CurrentUserSubstitution currentUserSubstitution;

    /**
     * Renders user menu button.
     *
     * <p>This method is safe to override. Override to customize user menu button rendering.
     *
     * @param userDetails user details
     * @return user menu button component
     */
    @Install(to = "userMenu", subject = "buttonRenderer")
    @Nullable
    @VisibleForTesting
    Component userMenuButtonRenderer(final UserDetails userDetails) {
        if (!(userDetails instanceof User user)) {
            log.debug("User menu button renderer: user is not instance of User entity");
            return null;
        }

        final String userName = generateUserName(user);
        log.debug("Rendering user menu button: username={}, displayName={}", user.getUsername(), userName);

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

    /**
     * Renders user menu header.
     *
     * <p>This method is safe to override. Override to customize user menu header rendering.
     *
     * @param userDetails user details
     * @return user menu header component
     */
    @Install(to = "userMenu", subject = "headerRenderer")
    @Nullable
    @VisibleForTesting
    Component userMenuHeaderRenderer(final UserDetails userDetails) {
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

        if (Objects.equals(user.getUsername(), name)) {
            text.addClassNames("user-menu-text-subtext");
        } else {
            final Span subtext = uiComponents.create(Span.class);
            subtext.setText(user.getUsername());
            subtext.setClassName("user-menu-subtext");

            content.add(subtext);
        }

        return content;
    }

    /**
     * Creates avatar component.
     *
     * <p>This method is safe to override. Override to customize avatar creation.
     *
     * @param fullName full name for avatar
     * @return avatar component
     */
    @VisibleForTesting
    Avatar createAvatar(final String fullName) {
        final Avatar avatar = uiComponents.create(Avatar.class);
        avatar.setName(fullName);
        avatar.getElement().setAttribute("tabindex", TABINDEX_EXCLUDED);
        avatar.setClassName("user-menu-avatar");

        return avatar;
    }

    /**
     * Generates user name from user entity.
     *
     * <p>This method is safe to override. Override to customize user name generation.
     *
     * @param user user entity
     * @return generated user name
     */
    @VisibleForTesting
    String generateUserName(final User user) {
        final String firstName = Objects.requireNonNullElse(user.getFirstName(), "");
        final String lastName = Objects.requireNonNullElse(user.getLastName(), "");
        final String userName = "%s %s".formatted(firstName, lastName).trim();

        return userName.isBlank() ? user.getUsername() : userName;
    }

    /**
     * Checks if user is substituted.
     *
     * <p>This method is safe to override. Override to customize substitution detection logic.
     *
     * @param user user to check
     * @return true if user is substituted
     */
    // SpotBugs RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE excluded via config/spotbugs/exclude.xml
    // The null check is intentional for defensive programming - getAuthenticatedUser() can return null
    @VisibleForTesting
    boolean isSubstituted(final User user) {
        if (user == null) {
            return false;
        }
        final UserDetails authenticatedUser = currentUserSubstitution.getAuthenticatedUser();
        if (authenticatedUser == null) {
            return false;
        }
        final String authenticatedUsername = authenticatedUser.getUsername();
        final boolean isSubstituted = !Objects.equals(authenticatedUsername, user.getUsername());
        if (isSubstituted) {
            log.debug(
                    "User substitution detected: authenticated={}, displayed={}",
                    authenticatedUsername,
                    user.getUsername());
        }
        return isSubstituted;
    }
}
