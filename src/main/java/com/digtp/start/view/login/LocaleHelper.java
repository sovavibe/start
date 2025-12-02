package com.digtp.start.view.login;

import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.server.VaadinSession;
import io.jmix.core.CoreProperties;
import io.jmix.core.MessageTools;
import io.jmix.flowui.component.loginform.JmixLoginForm;
import io.jmix.flowui.kit.component.ComponentUtils;
import io.jmix.flowui.kit.component.loginform.JmixLoginI18n;
import io.jmix.flowui.view.MessageBundle;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Helper class for locale management in login view.
 *
 * <p>Handles locale initialization and internationalization for login form.
 */
@Component
@RequiredArgsConstructor
public class LocaleHelper {

    private final CoreProperties coreProperties;
    private final MessageTools messageTools;

    /**
     * Initializes locale selection for login form.
     *
     * @param login the login form component
     */
    @SuppressWarnings("PMD.LooseCoupling") // LinkedHashMap needed to preserve insertion order
    public void initLocales(final JmixLoginForm login) {
        final LinkedHashMap<Locale, String> locales = coreProperties.getAvailableLocales().stream()
                .collect(Collectors.toMap(
                        Function.identity(), messageTools::getLocaleDisplayName, (s1, s2) -> s1, LinkedHashMap::new));

        ComponentUtils.setItemsMap(login, locales);
        login.setSelectedLocale(VaadinSession.getCurrent().getLocale());
    }

    /**
     * Updates login form internationalization for locale change.
     *
     * @param login the login form component
     * @param messageBundle the message bundle for translations
     * @param event the locale change event
     */
    public void updateLoginI18n(
            final JmixLoginForm login, final MessageBundle messageBundle, final LocaleChangeEvent event) {
        final JmixLoginI18n loginI18n = JmixLoginI18n.createDefault();

        final JmixLoginI18n.JmixForm form = new JmixLoginI18n.JmixForm();
        form.setTitle(messageBundle.getMessage("loginForm.headerTitle"));
        form.setUsername(messageBundle.getMessage("loginForm.username"));
        form.setPassword(messageBundle.getMessage("loginForm.password"));
        form.setSubmit(messageBundle.getMessage("loginForm.submit"));
        form.setForgotPassword(messageBundle.getMessage("loginForm.forgotPassword"));
        form.setRememberMe(messageBundle.getMessage("loginForm.rememberMe"));
        loginI18n.setForm(form);

        final LoginI18n.ErrorMessage errorMessage = new LoginI18n.ErrorMessage();
        errorMessage.setTitle(messageBundle.getMessage("loginForm.errorTitle"));
        errorMessage.setMessage(messageBundle.getMessage("loginForm.badCredentials"));
        errorMessage.setUsername(messageBundle.getMessage("loginForm.errorUsername"));
        errorMessage.setPassword(messageBundle.getMessage("loginForm.errorPassword"));
        loginI18n.setErrorMessage(errorMessage);

        login.setI18n(loginI18n);
    }
}
