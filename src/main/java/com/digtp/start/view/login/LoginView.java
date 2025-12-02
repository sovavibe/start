package com.digtp.start.view.login;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.AbstractLogin.LoginEvent;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import io.jmix.core.security.AccessDeniedException;
import io.jmix.flowui.component.loginform.JmixLoginForm;
import io.jmix.flowui.view.MessageBundle;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import io.jmix.securityflowui.authentication.AuthDetails;
import io.jmix.securityflowui.authentication.LoginViewSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

/**
 * Login view for user authentication.
 *
 * <p>Provides login form with username/password authentication, locale selection,
 * and remember-me functionality. Handles authentication errors and displays
 * appropriate messages to users.
 */
@Route(value = "login")
@AnonymousAllowed
@ViewController(id = "LoginView")
@ViewDescriptor(path = "login-view.xml")
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings(
        "java:S1948") // Framework pattern: Vaadin views contain non-serializable deps. Required for Gradle SonarLint
// plugin.
public class LoginView extends StandardView implements LocaleChangeObserver {

    private final transient LoginViewSupport loginViewSupport;
    private final transient LocaleHelper localeHelper;

    @ViewComponent
    private JmixLoginForm login;

    @ViewComponent
    private MessageBundle messageBundle;

    @Value("${ui.login.defaultUsername:}")
    private String defaultUsername;

    @Value("${ui.login.defaultPassword:}")
    private String defaultPassword;

    @Subscribe
    public void onInit(final InitEvent event) {
        log.debug("Login view initialized");
        localeHelper.initLocales(login);
        initDefaultCredentials();
    }

    private void initDefaultCredentials() {
        if (StringUtils.isNotBlank(defaultUsername)) {
            login.setUsername(defaultUsername);
        }

        if (StringUtils.isNotBlank(defaultPassword)) {
            login.setPassword(defaultPassword);
        }
    }

    @Subscribe("login")
    public void onLogin(final LoginEvent event) {
        try {
            loginViewSupport.authenticate(AuthDetails.of(event.getUsername(), event.getPassword())
                    .withLocale(login.getSelectedLocale())
                    .withRememberMe(login.isRememberMe()));
            log.info("User '{}' successfully logged in", event.getUsername());
        } catch (final BadCredentialsException | DisabledException | LockedException | AccessDeniedException e) {
            handleLoginFailure(event, e);
        }
    }

    private void handleLoginFailure(final LoginEvent event, final Exception e) {
        final String reason = getLoginFailureReason(e);
        log.warn("Login failed for user '{}': {}", event.getUsername(), reason);
        event.getSource().setError(true);
    }

    private String getLoginFailureReason(final Exception e) {
        return switch (e) {
            case BadCredentialsException ignored -> "invalid credentials";
            case DisabledException ignored -> "account disabled";
            case LockedException ignored -> "account locked";
            case AccessDeniedException ignored -> "access denied";
            default -> "unknown error";
        };
    }

    @Override
    public void localeChange(final LocaleChangeEvent event) {
        UI.getCurrent().getPage().setTitle(messageBundle.getMessage("LoginView.title"));
        localeHelper.updateLoginI18n(login, messageBundle, event);
    }
}
