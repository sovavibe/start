/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.security;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(AuthenticatedAsAdmin.class)
// Framework patterns suppressed via @SuppressWarnings (Palantir Baseline defaults):
// - PMD.CommentRequired, PMD.CommentDefaultAccessModifier, PMD.AtLeastOneConstructor
// - PMD.LongVariable, PMD.UnitTestContainsTooManyAsserts, PMD.UnitTestAssertionsShouldIncludeMessage
// - PMD.LawOfDemeter, PMD.AvoidDuplicateLiterals
class StartPasswordValidatorTest extends AbstractIntegrationTest {

    @Autowired
    private StartPasswordValidator passwordValidator;

    @Autowired
    private DataManager dataManager;

    private User savedUser;

    /**
     * Creates a typed mock for PasswordValidationContext to avoid unchecked warnings.
     * This is a helper method that centralizes the unchecked cast in one place.
     */
    @SuppressWarnings("unchecked") // Generic type erasure requires unchecked cast
    private PasswordValidationContext<User> createPasswordContext() {
        return mock(PasswordValidationContext.class);
    }

    @Test
    void testValidateValidPassword() {
        // Arrange
        final String validPassword = "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH);
        final PasswordValidationContext<User> context = createPasswordContext();
        when(context.getPassword()).thenReturn(validPassword);
        when(context.getUser()).thenReturn(null);

        // Act & Assert
        assertThatCode(() -> passwordValidator.validate(context)).doesNotThrowAnyException();
    }

    @Test
    void testValidateValidPasswordWithUser() {
        // Arrange
        final User user = dataManager.create(User.class);
        user.setUsername("test-user-" + System.currentTimeMillis());
        savedUser = dataManager.save(user);

        final String validPassword = "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH);
        final PasswordValidationContext<User> context = createPasswordContext();
        when(context.getPassword()).thenReturn(validPassword);
        when(context.getUser()).thenReturn(user);

        // Act & Assert
        assertThatCode(() -> passwordValidator.validate(context)).doesNotThrowAnyException();
    }

    @Test
    void testValidateNullPassword() {
        // Arrange
        final PasswordValidationContext<User> context = createPasswordContext();
        when(context.getPassword()).thenReturn(null);
        when(context.getUser()).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> passwordValidator.validate(context))
                .isInstanceOf(PasswordValidationException.class)
                .hasMessage("Password cannot be empty");
    }

    @Test
    void testValidateEmptyPassword() {
        // Arrange
        final PasswordValidationContext<User> context = createPasswordContext();
        when(context.getPassword()).thenReturn("");
        when(context.getUser()).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> passwordValidator.validate(context))
                .isInstanceOf(PasswordValidationException.class)
                .hasMessage("Password cannot be empty");
    }

    @Test
    void testValidatePasswordTooShort() {
        // Arrange
        final String shortPassword = "short";
        final PasswordValidationContext<User> context = createPasswordContext();
        when(context.getPassword()).thenReturn(shortPassword);
        when(context.getUser()).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> passwordValidator.validate(context))
                .isInstanceOf(PasswordValidationException.class)
                .hasMessageContaining("at least " + SecurityConstants.MIN_PASSWORD_LENGTH);
    }

    @Test
    void testValidatePasswordBoundary() {
        // Arrange
        final String boundaryPassword = "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH - 1);
        final PasswordValidationContext<User> context = createPasswordContext();
        when(context.getPassword()).thenReturn(boundaryPassword);
        when(context.getUser()).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> passwordValidator.validate(context))
                .isInstanceOf(PasswordValidationException.class)
                .hasMessageContaining("at least " + SecurityConstants.MIN_PASSWORD_LENGTH);
    }

    @Test
    void testValidatePasswordExactMinimumLength() {
        // Arrange
        final String exactPassword = "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH);
        final PasswordValidationContext<User> context = createPasswordContext();
        when(context.getPassword()).thenReturn(exactPassword);
        when(context.getUser()).thenReturn(null);

        // Act & Assert
        assertThatCode(() -> passwordValidator.validate(context)).doesNotThrowAnyException();
    }

    @Test
    @SuppressWarnings("java:S4144") // Test method has similar structure but tests different scenario (null user)
    void testValidatePasswordWithNullUser() {
        // Arrange
        final String validPassword = "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH);
        final PasswordValidationContext<User> context = createPasswordContext();
        when(context.getPassword()).thenReturn(validPassword);
        when(context.getUser()).thenReturn(null);

        // Act & Assert
        assertThatCode(() -> passwordValidator.validate(context)).doesNotThrowAnyException();
    }

    @AfterEach
    void afterEach() {
        if (savedUser != null) {
            dataManager.remove(savedUser);
            savedUser = null; // NOPMD - NullAssignment: prevents accidental reuse of removed entity
        }
    }
}
