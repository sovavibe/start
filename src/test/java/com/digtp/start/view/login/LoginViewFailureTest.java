/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.view.login;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.test.context.ActiveProfiles;

import com.digtp.start.StartApplication;
import com.digtp.start.testsupport.AbstractIntegrationTest;
import com.digtp.start.testsupport.AuthenticatedAsAdmin;
import com.vaadin.flow.component.login.AbstractLogin.LoginEvent;
import com.vaadin.flow.component.login.LoginForm;

import io.jmix.core.security.AccessDeniedException;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.testassist.FlowuiTestAssistConfiguration;
import io.jmix.flowui.testassist.UiTest;
import io.jmix.flowui.testassist.UiTestUtils;
import io.jmix.flowui.view.View;
import io.jmix.securityflowui.authentication.LoginViewSupport;

/**
 * Unit tests for LoginView failure scenarios.
 *
 * <p>Tests different authentication failure cases to ensure all exception types are handled.
 */
@UiTest
@SpringBootTest(classes = {StartApplication.class, FlowuiTestAssistConfiguration.class})
@ActiveProfiles("test")
@ExtendWith(AuthenticatedAsAdmin.class)
// Framework patterns suppressed via @SuppressWarnings (Palantir Baseline defaults):
// - PMD.CommentRequired, PMD.CommentDefaultAccessModifier, PMD.AtLeastOneConstructor
// - PMD.LongVariable, PMD.UnitTestContainsTooManyAsserts, PMD.UnitTestAssertionsShouldIncludeMessage
// - PMD.LawOfDemeter
@SuppressWarnings("PMD.AvoidAccessibilityAlteration") // Test: reflection to call private onLogin method
class LoginViewFailureTest extends AbstractIntegrationTest {

    private static final String ON_LOGIN_METHOD = "onLogin";

    @Autowired
    ViewNavigators viewNavigators;

    // DeprecatedForRemovalApiUsage disabled in build.gradle (Error Prone)
    @SuppressWarnings("java:S5738") // @MockBean is Spring Boot standard, still supported
    @MockBean
    LoginViewSupport loginViewSupport;

    @Test
    void testLoginFailureBadCredentials() throws ReflectiveOperationException {
        // Arrange
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();
        final View<?> loginView = getCurrentViewAsView();
        UiTestUtils.getComponent(loginView, "login"); // Verify component exists

        when(loginViewSupport.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        final LoginEvent loginEvent = mock(LoginEvent.class);
        final LoginForm loginForm = mock(LoginForm.class);
        when(loginEvent.getSource()).thenReturn(loginForm);
        when(loginEvent.getUsername()).thenReturn("testuser");
        when(loginEvent.getPassword()).thenReturn("wrongpassword");

        // Act - use reflection to call onLogin method
        final Method method = loginView.getClass().getMethod(ON_LOGIN_METHOD, LoginEvent.class);
        method.invoke(loginView, loginEvent);

        // Assert
        verify(loginForm).setError(true);
    }

    @Test
    void testLoginFailureDisabledException() throws ReflectiveOperationException {
        // Arrange
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();
        final View<?> loginView = getCurrentViewAsView();

        when(loginViewSupport.authenticate(any())).thenThrow(new DisabledException("Account disabled"));

        final LoginEvent loginEvent = mock(LoginEvent.class);
        final LoginForm loginForm = mock(LoginForm.class);
        when(loginEvent.getSource()).thenReturn(loginForm);
        when(loginEvent.getUsername()).thenReturn("disableduser");

        // Act - use reflection to call onLogin method
        final Method method = loginView.getClass().getMethod(ON_LOGIN_METHOD, LoginEvent.class);
        method.invoke(loginView, loginEvent);

        // Assert
        verify(loginForm).setError(true);
    }

    @Test
    void testLoginFailureLockedException() throws ReflectiveOperationException {
        // Arrange
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();
        final View<?> loginView = getCurrentViewAsView();

        when(loginViewSupport.authenticate(any())).thenThrow(new LockedException("Account locked"));

        final LoginEvent loginEvent = mock(LoginEvent.class);
        final LoginForm loginForm = mock(LoginForm.class);
        when(loginEvent.getSource()).thenReturn(loginForm);
        when(loginEvent.getUsername()).thenReturn("lockeduser");

        // Act - use reflection to call onLogin method
        final Method method = loginView.getClass().getMethod(ON_LOGIN_METHOD, LoginEvent.class);
        method.invoke(loginView, loginEvent);

        // Assert
        verify(loginForm).setError(true);
    }

    @Test
    void testLoginFailureAccessDeniedException() throws ReflectiveOperationException {
        // Arrange
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();
        final View<?> loginView = getCurrentViewAsView();

        final AccessDeniedException accessDeniedException = new AccessDeniedException("Access denied", "resource");
        when(loginViewSupport.authenticate(any())).thenThrow(accessDeniedException);

        final LoginEvent loginEvent = mock(LoginEvent.class);
        final LoginForm loginForm = mock(LoginForm.class);
        when(loginEvent.getSource()).thenReturn(loginForm);
        when(loginEvent.getUsername()).thenReturn("denieduser");

        // Act - use reflection to call onLogin method
        final Method method = loginView.getClass().getMethod(ON_LOGIN_METHOD, LoginEvent.class);
        method.invoke(loginView, loginEvent);

        // Assert
        verify(loginForm).setError(true);
    }

    // Note: Unknown exceptions (not in catch block) are not handled by onLogin
    // They propagate up and are handled by framework error handling
    // Testing this would require more complex setup with exception handlers
}
