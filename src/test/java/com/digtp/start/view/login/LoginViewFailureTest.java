/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.view.login;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.digtp.start.StartApplication;
import com.digtp.start.testsupport.AbstractIntegrationTest;
import com.digtp.start.testsupport.AuthenticatedAsAdmin;
import com.vaadin.flow.component.login.AbstractLogin.LoginEvent;
import io.jmix.core.security.AccessDeniedException;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.component.loginform.JmixLoginForm;
import io.jmix.flowui.testassist.FlowuiTestAssistConfiguration;
import io.jmix.flowui.testassist.UiTest;
import io.jmix.flowui.testassist.UiTestUtils;
import io.jmix.securityflowui.authentication.LoginViewSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.test.context.ActiveProfiles;

/**
 * Unit tests for LoginView failure scenarios.
 *
 * <p>Tests different authentication failure cases to ensure all exception types are handled.
 */
@UiTest
@SpringBootTest(classes = {StartApplication.class, FlowuiTestAssistConfiguration.class})
@ActiveProfiles("test")
@ExtendWith(AuthenticatedAsAdmin.class)
// Test: LoginEvent.getPassword() is deprecated in Vaadin API but still used in production code
@SuppressWarnings({
    // Test: LoginEvent.getPassword() is deprecated in Vaadin API but still used in production code
    "removal", // deprecated API
    // Test: @MockBean is Spring Boot standard for mocking beans in tests
    "java:S5738", // deprecated annotation
    // Test: Some tests are clearer as separate methods rather than parameterized
    "java:S5976", // parameterized test
    // Test: Multiple assertions on same object are acceptable in tests for clarity
    "java:S5853", // multiple assertions
    // Test: Test methods may have similar structure but test different scenarios
    "java:S4144" // similar methods
})
class LoginViewFailureTest extends AbstractIntegrationTest {

    @Autowired
    ViewNavigators viewNavigators;

    @MockBean
    LoginViewSupport loginViewSupport;

    @Test
    void testLoginFailureBadCredentials() {
        // Arrange
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();
        final LoginView loginView = (LoginView) getCurrentViewAsView();
        UiTestUtils.getComponent(loginView, "login"); // Verify component exists

        when(loginViewSupport.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        final LoginEvent loginEvent = mock(LoginEvent.class);
        final JmixLoginForm loginForm = mock(JmixLoginForm.class);
        when(loginEvent.getSource()).thenReturn(loginForm);
        when(loginEvent.getUsername()).thenReturn("testuser");
        when(loginEvent.getPassword()).thenReturn("wrongpassword");

        // Act - call onLogin method directly (public @Subscribe method)
        loginView.onLogin(loginEvent);

        // Assert
        verify(loginForm).setError(true);
    }

    @Test
    void testLoginFailureDisabledException() {
        // Arrange
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();
        final LoginView loginView = (LoginView) getCurrentViewAsView();

        when(loginViewSupport.authenticate(any())).thenThrow(new DisabledException("Account disabled"));

        final LoginEvent loginEvent = mock(LoginEvent.class);
        final JmixLoginForm loginForm = mock(JmixLoginForm.class);
        when(loginEvent.getSource()).thenReturn(loginForm);
        when(loginEvent.getUsername()).thenReturn("disableduser");

        // Act - call onLogin method directly (public @Subscribe method)
        loginView.onLogin(loginEvent);

        // Assert
        verify(loginForm).setError(true);
    }

    @Test
    void testLoginFailureLockedException() {
        // Arrange
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();
        final LoginView loginView = (LoginView) getCurrentViewAsView();

        when(loginViewSupport.authenticate(any())).thenThrow(new LockedException("Account locked"));

        final LoginEvent loginEvent = mock(LoginEvent.class);
        final JmixLoginForm loginForm = mock(JmixLoginForm.class);
        when(loginEvent.getSource()).thenReturn(loginForm);
        when(loginEvent.getUsername()).thenReturn("lockeduser");

        // Act - call onLogin method directly (public @Subscribe method)
        loginView.onLogin(loginEvent);

        // Assert
        verify(loginForm).setError(true);
    }

    @Test
    void testLoginFailureAccessDeniedException() {
        // Arrange
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();
        final LoginView loginView = (LoginView) getCurrentViewAsView();

        final AccessDeniedException accessDeniedException = new AccessDeniedException("Access denied", "resource");
        when(loginViewSupport.authenticate(any())).thenThrow(accessDeniedException);

        final LoginEvent loginEvent = mock(LoginEvent.class);
        final JmixLoginForm loginForm = mock(JmixLoginForm.class);
        when(loginEvent.getSource()).thenReturn(loginForm);
        when(loginEvent.getUsername()).thenReturn("denieduser");

        // Act - call onLogin method directly (public @Subscribe method)
        loginView.onLogin(loginEvent);

        // Assert
        verify(loginForm).setError(true);
    }

    // Note: Unknown exceptions (not in catch block) are not handled by onLogin
    // They propagate up and are handled by framework error handling
    // Testing this would require more complex setup with exception handlers
}
