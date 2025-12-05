/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.view.login;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.server.VaadinSession;
import io.jmix.core.CoreProperties;
import io.jmix.core.MessageTools;
import io.jmix.flowui.component.loginform.JmixLoginForm;
import io.jmix.flowui.kit.component.loginform.JmixLoginI18n;
import io.jmix.flowui.view.MessageBundle;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for LocaleHelper.
 *
 * <p>Tests locale initialization and internationalization updates for login form.
 */
@ExtendWith(MockitoExtension.class)
class LocaleHelperTest {

    @Mock
    private CoreProperties coreProperties;

    @Mock
    private MessageTools messageTools;

    @Mock
    private JmixLoginForm login;

    @Mock
    private MessageBundle messageBundle;

    @Mock
    private LocaleChangeEvent localeChangeEvent;

    @Mock
    private VaadinSession vaadinSession;

    private LocaleHelper localeHelper;

    @BeforeEach
    void beforeEach() {
        localeHelper = new LocaleHelper(coreProperties, messageTools);
    }

    @Test
    void testInitLocales() {
        // Arrange
        final Locale locale1 = Locale.ENGLISH;
        final Locale locale2 = Locale.forLanguageTag("ru");
        final List<Locale> availableLocales = List.of(locale1, locale2);
        final Locale currentLocale = locale1;

        when(coreProperties.getAvailableLocales()).thenReturn(availableLocales);
        when(messageTools.getLocaleDisplayName(locale1)).thenReturn("English");
        when(messageTools.getLocaleDisplayName(locale2)).thenReturn("Russian");
        when(vaadinSession.getLocale()).thenReturn(currentLocale);

        // Act
        try (MockedStatic<VaadinSession> vaadinSessionMock = Mockito.mockStatic(VaadinSession.class)) {
            vaadinSessionMock.when(VaadinSession::getCurrent).thenReturn(vaadinSession);
            localeHelper.initLocales(login);
        }

        // Assert
        verify(coreProperties).getAvailableLocales();
        verify(messageTools).getLocaleDisplayName(locale1);
        verify(messageTools).getLocaleDisplayName(locale2);
        verify(login).setSelectedLocale(currentLocale);
    }

    @Test
    void testInitLocalesWithEmptyList() {
        // Arrange
        when(coreProperties.getAvailableLocales()).thenReturn(List.of());
        when(vaadinSession.getLocale()).thenReturn(Locale.ENGLISH);

        // Act
        try (MockedStatic<VaadinSession> vaadinSessionMock = Mockito.mockStatic(VaadinSession.class)) {
            vaadinSessionMock.when(VaadinSession::getCurrent).thenReturn(vaadinSession);
            localeHelper.initLocales(login);
        }

        // Assert
        verify(coreProperties).getAvailableLocales();
        verify(messageTools, never()).getLocaleDisplayName(ArgumentMatchers.any());
        verify(login).setSelectedLocale(Locale.ENGLISH);
    }

    @Test
    void testUpdateLoginI18n() {
        // Arrange
        final String headerTitle = "Login";
        final String username = "Username";
        final String password = "Password";
        final String submit = "Submit";
        final String forgotPassword = "Forgot Password";
        final String rememberMe = "Remember Me";
        final String errorTitle = "Error";
        final String badCredentials = "Bad Credentials";
        final String errorUsername = "Username Error";
        final String errorPassword = "Password Error";

        when(messageBundle.getMessage("loginForm.headerTitle")).thenReturn(headerTitle);
        when(messageBundle.getMessage("loginForm.username")).thenReturn(username);
        when(messageBundle.getMessage("loginForm.password")).thenReturn(password);
        when(messageBundle.getMessage("loginForm.submit")).thenReturn(submit);
        when(messageBundle.getMessage("loginForm.forgotPassword")).thenReturn(forgotPassword);
        when(messageBundle.getMessage("loginForm.rememberMe")).thenReturn(rememberMe);
        when(messageBundle.getMessage("loginForm.errorTitle")).thenReturn(errorTitle);
        when(messageBundle.getMessage("loginForm.badCredentials")).thenReturn(badCredentials);
        when(messageBundle.getMessage("loginForm.errorUsername")).thenReturn(errorUsername);
        when(messageBundle.getMessage("loginForm.errorPassword")).thenReturn(errorPassword);

        // Act
        localeHelper.updateLoginI18n(login, messageBundle, localeChangeEvent);

        // Assert
        verify(messageBundle).getMessage("loginForm.headerTitle");
        verify(messageBundle).getMessage("loginForm.username");
        verify(messageBundle).getMessage("loginForm.password");
        verify(messageBundle).getMessage("loginForm.submit");
        verify(messageBundle).getMessage("loginForm.forgotPassword");
        verify(messageBundle).getMessage("loginForm.rememberMe");
        verify(messageBundle).getMessage("loginForm.errorTitle");
        verify(messageBundle).getMessage("loginForm.badCredentials");
        verify(messageBundle).getMessage("loginForm.errorUsername");
        verify(messageBundle).getMessage("loginForm.errorPassword");
        verify(login).setI18n(ArgumentMatchers.any(JmixLoginI18n.class));
    }
}
