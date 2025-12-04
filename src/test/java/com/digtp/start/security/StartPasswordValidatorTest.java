/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.security;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.digtp.start.config.SecurityConstants;
import com.digtp.start.entity.User;
import com.digtp.start.testsupport.AbstractIntegrationTest;
import com.digtp.start.testsupport.AuthenticatedAsAdmin;
import io.jmix.core.DataManager;
import io.jmix.securityflowui.password.PasswordValidationContext;
import io.jmix.securityflowui.password.PasswordValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith({AuthenticatedAsAdmin.class, MockitoExtension.class})
// Framework patterns suppressed via @SuppressWarnings (Palantir Baseline defaults):
// - PMD.CommentRequired, PMD.CommentDefaultAccessModifier, PMD.AtLeastOneConstructor
// - PMD.LongVariable, PMD.UnitTestContainsTooManyAsserts, PMD.UnitTestAssertionsShouldIncludeMessage
// - PMD.LawOfDemeter, PMD.AvoidDuplicateLiterals
class StartPasswordValidatorTest extends AbstractIntegrationTest {

    @Autowired
    private StartPasswordValidator passwordValidator;

    @Autowired
    private DataManager dataManager;

    @Mock
    private PasswordValidationContext<User> passwordContext;

    private User savedUser;

    @Test
    void testValidateValidPassword() {
        // Arrange
        final String validPassword = "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH);
        when(passwordContext.getPassword()).thenReturn(validPassword);
        when(passwordContext.getUser()).thenReturn(null);

        // Act & Assert
        assertThatCode(() -> passwordValidator.validate(passwordContext)).doesNotThrowAnyException();
    }

    @Test
    void testValidateValidPasswordWithUser() {
        // Arrange
        final User user = dataManager.create(User.class);
        user.setUsername("test-user-" + System.currentTimeMillis());
        savedUser = dataManager.save(user);

        final String validPassword = "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH);
        when(passwordContext.getPassword()).thenReturn(validPassword);
        when(passwordContext.getUser()).thenReturn(user);

        // Act & Assert
        assertThatCode(() -> passwordValidator.validate(passwordContext)).doesNotThrowAnyException();
    }

    @Test
    void testValidateNullPassword() {
        // Arrange
        lenient().when(passwordContext.getPassword()).thenReturn(null);
        lenient().when(passwordContext.getUser()).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> passwordValidator.validate(passwordContext))
                .isInstanceOf(PasswordValidationException.class)
                .hasMessage("Password cannot be empty");
    }

    @Test
    void testValidateEmptyPassword() {
        // Arrange
        lenient().when(passwordContext.getPassword()).thenReturn("");
        lenient().when(passwordContext.getUser()).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> passwordValidator.validate(passwordContext))
                .isInstanceOf(PasswordValidationException.class)
                .hasMessage("Password cannot be empty");
    }

    @Test
    void testValidatePasswordTooShort() {
        // Arrange
        final String shortPassword = "short";
        lenient().when(passwordContext.getPassword()).thenReturn(shortPassword);
        lenient().when(passwordContext.getUser()).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> passwordValidator.validate(passwordContext))
                .isInstanceOf(PasswordValidationException.class)
                .hasMessageContaining("at least " + SecurityConstants.MIN_PASSWORD_LENGTH);
    }

    @Test
    void testValidatePasswordBoundary() {
        // Arrange
        final String boundaryPassword = "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH - 1);
        lenient().when(passwordContext.getPassword()).thenReturn(boundaryPassword);
        lenient().when(passwordContext.getUser()).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> passwordValidator.validate(passwordContext))
                .isInstanceOf(PasswordValidationException.class)
                .hasMessageContaining("at least " + SecurityConstants.MIN_PASSWORD_LENGTH);
    }

    @Test
    void testValidatePasswordExactMinimumLength() {
        // Arrange
        final String exactPassword = "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH);
        when(passwordContext.getPassword()).thenReturn(exactPassword);
        when(passwordContext.getUser()).thenReturn(null);

        // Act & Assert
        assertThatCode(() -> passwordValidator.validate(passwordContext)).doesNotThrowAnyException();
    }

    @Test
    @SuppressWarnings("java:S4144") // Test method has similar structure but tests different scenario (null user)
    void testValidatePasswordWithNullUser() {
        // Arrange
        final String validPassword = "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH);
        when(passwordContext.getPassword()).thenReturn(validPassword);
        when(passwordContext.getUser()).thenReturn(null);

        // Act & Assert
        assertThatCode(() -> passwordValidator.validate(passwordContext)).doesNotThrowAnyException();
    }

    @AfterEach
    void afterEach() {
        if (savedUser != null) {
            dataManager.remove(savedUser);
            savedUser = null; // NOPMD - NullAssignment: prevents accidental reuse of removed entity
        }
    }
}
