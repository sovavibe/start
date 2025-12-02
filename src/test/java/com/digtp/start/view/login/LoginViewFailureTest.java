package com.digtp.start.view.login;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.digtp.start.StartApplication;
import com.digtp.start.test_support.AbstractIntegrationTest;
import com.vaadin.flow.component.login.AbstractLogin.LoginEvent;
import com.vaadin.flow.component.login.LoginForm;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.component.loginform.JmixLoginForm;
import io.jmix.flowui.testassist.FlowuiTestAssistConfiguration;
import io.jmix.flowui.testassist.UiTest;
import io.jmix.flowui.testassist.UiTestUtils;
import io.jmix.securityflowui.authentication.LoginViewSupport;
import org.junit.jupiter.api.Test;
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
class LoginViewFailureTest extends AbstractIntegrationTest {

    @Autowired
    ViewNavigators viewNavigators;

    @MockBean
    LoginViewSupport loginViewSupport;

    @Test
    void testLoginFailureBadCredentials() throws Exception {
        // Arrange
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();
        final LoginView loginView = UiTestUtils.getCurrentView();
        final JmixLoginForm login = UiTestUtils.getComponent(loginView, "login");

        when(loginViewSupport.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        final LoginEvent loginEvent = mock(LoginEvent.class);
        final LoginForm loginForm = mock(LoginForm.class);
        when(loginEvent.getSource()).thenReturn(loginForm);
        when(loginEvent.getUsername()).thenReturn("testuser");
        when(loginEvent.getPassword()).thenReturn("wrongpassword");

        // Act
        loginView.onLogin(loginEvent);

        // Assert
        verify(loginForm).setError(true);
    }

    @Test
    void testLoginFailureDisabledException() throws Exception {
        // Arrange
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();
        final LoginView loginView = UiTestUtils.getCurrentView();

        when(loginViewSupport.authenticate(any())).thenThrow(new DisabledException("Account disabled"));

        final LoginEvent loginEvent = mock(LoginEvent.class);
        final LoginForm loginForm = mock(LoginForm.class);
        when(loginEvent.getSource()).thenReturn(loginForm);
        when(loginEvent.getUsername()).thenReturn("disableduser");

        // Act
        loginView.onLogin(loginEvent);

        // Assert
        verify(loginForm).setError(true);
    }

    @Test
    void testLoginFailureLockedException() throws Exception {
        // Arrange
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();
        final LoginView loginView = UiTestUtils.getCurrentView();

        when(loginViewSupport.authenticate(any())).thenThrow(new LockedException("Account locked"));

        final LoginEvent loginEvent = mock(LoginEvent.class);
        final LoginForm loginForm = mock(LoginForm.class);
        when(loginEvent.getSource()).thenReturn(loginForm);
        when(loginEvent.getUsername()).thenReturn("lockeduser");

        // Act
        loginView.onLogin(loginEvent);

        // Assert
        verify(loginForm).setError(true);
    }

    @Test
    void testLoginFailureAccessDeniedException() throws Exception {
        // Arrange
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();
        final LoginView loginView = UiTestUtils.getCurrentView();

        final io.jmix.core.security.AccessDeniedException accessDeniedException =
                new io.jmix.core.security.AccessDeniedException("Access denied", "resource");
        when(loginViewSupport.authenticate(any())).thenThrow(accessDeniedException);

        final LoginEvent loginEvent = mock(LoginEvent.class);
        final LoginForm loginForm = mock(LoginForm.class);
        when(loginEvent.getSource()).thenReturn(loginForm);
        when(loginEvent.getUsername()).thenReturn("denieduser");

        // Act
        loginView.onLogin(loginEvent);

        // Assert
        verify(loginForm).setError(true);
    }

    // Note: Unknown exceptions (not in catch block) are not handled by onLogin
    // They propagate up and are handled by framework error handling
    // Testing this would require more complex setup with exception handlers
}
