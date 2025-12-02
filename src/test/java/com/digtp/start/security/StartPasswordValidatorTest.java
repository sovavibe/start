package com.digtp.start.security;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.digtp.start.config.SecurityConstants;
import com.digtp.start.entity.User;
import com.digtp.start.test_support.AbstractIntegrationTest;
import com.digtp.start.test_support.AuthenticatedAsAdmin;
import io.jmix.core.DataManager;
import io.jmix.securityflowui.password.PasswordValidationContext;
import io.jmix.securityflowui.password.PasswordValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(AuthenticatedAsAdmin.class)
class StartPasswordValidatorTest extends AbstractIntegrationTest {

    @Autowired
    private StartPasswordValidator passwordValidator;

    @Autowired
    private DataManager dataManager;

    private User savedUser;

    @Test
    @SuppressWarnings("unchecked") // Mockito mock requires unchecked cast
    void testValidateValidPassword() {
        // Arrange
        final String validPassword = "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH);
        final PasswordValidationContext<User> context = mock(PasswordValidationContext.class);
        when(context.getPassword()).thenReturn(validPassword);
        when(context.getUser()).thenReturn(null);

        // Act & Assert
        assertThatCode(() -> passwordValidator.validate(context)).doesNotThrowAnyException();
    }

    @Test
    @SuppressWarnings("unchecked") // Mockito mock requires unchecked cast
    void testValidateValidPasswordWithUser() {
        // Arrange
        final User user = dataManager.create(User.class);
        user.setUsername("test-user-" + System.currentTimeMillis());
        savedUser = dataManager.save(user);

        final String validPassword = "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH);
        final PasswordValidationContext<User> context = mock(PasswordValidationContext.class);
        when(context.getPassword()).thenReturn(validPassword);
        when(context.getUser()).thenReturn(user);

        // Act & Assert
        assertThatCode(() -> passwordValidator.validate(context)).doesNotThrowAnyException();
    }

    @Test
    @SuppressWarnings("unchecked") // Mockito mock requires unchecked cast
    void testValidateNullPassword() {
        // Arrange
        final PasswordValidationContext<User> context = mock(PasswordValidationContext.class);
        when(context.getPassword()).thenReturn(null);
        when(context.getUser()).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> passwordValidator.validate(context))
                .isInstanceOf(PasswordValidationException.class)
                .hasMessage("Password cannot be empty");
    }

    @Test
    @SuppressWarnings("unchecked") // Mockito mock requires unchecked cast
    void testValidateEmptyPassword() {
        // Arrange
        final PasswordValidationContext<User> context = mock(PasswordValidationContext.class);
        when(context.getPassword()).thenReturn("");
        when(context.getUser()).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> passwordValidator.validate(context))
                .isInstanceOf(PasswordValidationException.class)
                .hasMessage("Password cannot be empty");
    }

    @Test
    @SuppressWarnings("unchecked") // Mockito mock requires unchecked cast
    void testValidatePasswordTooShort() {
        // Arrange
        final String shortPassword = "short";
        final PasswordValidationContext<User> context = mock(PasswordValidationContext.class);
        when(context.getPassword()).thenReturn(shortPassword);
        when(context.getUser()).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> passwordValidator.validate(context))
                .isInstanceOf(PasswordValidationException.class)
                .hasMessageContaining("at least " + SecurityConstants.MIN_PASSWORD_LENGTH);
    }

    @Test
    @SuppressWarnings("unchecked") // Mockito mock requires unchecked cast
    void testValidatePasswordBoundary() {
        // Arrange
        final String boundaryPassword = "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH - 1);
        final PasswordValidationContext<User> context = mock(PasswordValidationContext.class);
        when(context.getPassword()).thenReturn(boundaryPassword);
        when(context.getUser()).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> passwordValidator.validate(context))
                .isInstanceOf(PasswordValidationException.class)
                .hasMessageContaining("at least " + SecurityConstants.MIN_PASSWORD_LENGTH);
    }

    @Test
    @SuppressWarnings("unchecked") // Mockito mock requires unchecked cast
    void testValidatePasswordExactMinimumLength() {
        // Arrange
        final String exactPassword = "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH);
        final PasswordValidationContext<User> context = mock(PasswordValidationContext.class);
        when(context.getPassword()).thenReturn(exactPassword);
        when(context.getUser()).thenReturn(null);

        // Act & Assert
        assertThatCode(() -> passwordValidator.validate(context)).doesNotThrowAnyException();
    }

    @Test
    @SuppressWarnings({"unchecked", "java:S4144"
    }) // Mockito mock requires unchecked cast; Intentional: Tests null user scenario
    void testValidatePasswordWithNullUser() {
        // Arrange
        final String validPassword = "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH);
        final PasswordValidationContext<User> context = mock(PasswordValidationContext.class);
        when(context.getPassword()).thenReturn(validPassword);
        when(context.getUser()).thenReturn(null);

        // Act & Assert
        assertThatCode(() -> passwordValidator.validate(context)).doesNotThrowAnyException();
    }

    @AfterEach
    void tearDown() {
        if (savedUser != null) {
            dataManager.remove(savedUser);
            savedUser = null; // NOPMD - NullAssignment: prevents accidental reuse of removed entity
        }
    }
}
