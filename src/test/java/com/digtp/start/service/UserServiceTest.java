/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.digtp.start.config.SecurityConstants;
import com.digtp.start.entity.User;
import com.digtp.start.testsupport.AbstractIntegrationTest;
import com.digtp.start.testsupport.AuthenticatedAsAdmin;
import com.digtp.start.testsupport.TestFixtures;
import com.palantir.logsafe.exceptions.SafeIllegalArgumentException;
import io.jmix.core.DataManager;
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
// - PMD.LawOfDemeter, PMD.TooManyMethods
class UserServiceTest extends AbstractIntegrationTest {

    private static final String EXISTING_ENCODED_PASSWORD = "existing-encoded-password";
    private static final String AT_LEAST = "at least ";

    @Autowired
    UserService userService;

    @Autowired
    DataManager dataManager;

    User savedUser;

    @Test
    void testEncodePassword() {
        // Arrange
        final String plainPassword = TestFixtures.DEFAULT_TEST_PASSWORD;

        // Act
        final String encodedPassword = userService.encodePassword(plainPassword);

        // Assert
        assertThat(encodedPassword).isNotBlank().isNotEqualTo(plainPassword);
        assertThat(encodedPassword.length()).isGreaterThan(plainPassword.length());
    }

    @Test
    void testEncodePasswordDifferentPasswordsProduceDifferentHashes() {
        // Arrange
        final String password1 = "password1";
        final String password2 = "password2";

        // Act
        final String encoded1 = userService.encodePassword(password1);
        final String encoded2 = userService.encodePassword(password2);

        // Assert
        assertThat(encoded1).isNotEqualTo(encoded2);
    }

    @Test
    // nullness:argument excluded via build.gradle (NullAway:ExcludedClasses for tests)
    void testValidatePasswordConfirmation() {
        // Arrange
        final String password = "password";
        final String matchingPassword = "password";
        final String differentPassword = "different";

        // Act & Assert - matching passwords
        assertThat(userService.validatePasswordConfirmation(password, matchingPassword))
                .isTrue();

        // Act & Assert - different passwords
        assertThat(userService.validatePasswordConfirmation(password, differentPassword))
                .isFalse();

        // Act & Assert - null confirmPassword - Objects.equals handles null correctly
        assertThat(userService.validatePasswordConfirmation(password, null)).isFalse();

        // Act & Assert - null password - Objects.equals handles null correctly
        assertThat(userService.validatePasswordConfirmation(null, password)).isFalse();

        // Act & Assert - both null - Objects.equals handles null correctly
        assertThat(userService.validatePasswordConfirmation(null, null)).isTrue();
    }

    @Test
    void testPrepareUserForSaveNewUser() {
        // Arrange
        final User user = dataManager.create(User.class);
        user.setUsername(TestFixtures.uniqueUsername());
        final String password = TestFixtures.DEFAULT_TEST_PASSWORD;

        // Act
        userService.prepareUserForSave(user, password, true);

        // Assert
        assertThat(user.getPassword()).isNotBlank().isNotEqualTo(password);
        savedUser = dataManager.save(user);
    }

    @Test
    void testPrepareUserForSaveExistingUser() {
        final User user = dataManager.create(User.class);
        user.setUsername(TestFixtures.uniqueUsername());
        user.setPassword(EXISTING_ENCODED_PASSWORD);
        savedUser = dataManager.save(user);

        final String originalPassword = user.getPassword();
        userService.prepareUserForSave(user, null, false);

        assertThat(user.getPassword()).isEqualTo(originalPassword);
    }

    @Test
    void testValidatePasswordStrengthValid() {
        final String validPassword = TestFixtures.validPassword();
        // Should not throw exception
        assertThatCode(() -> userService.validatePasswordStrength(validPassword))
                .doesNotThrowAnyException();
    }

    @Test
    void testValidatePasswordStrengthBoundary() {
        final String boundaryPassword = TestFixtures.shortPassword();
        assertThatThrownBy(() -> userService.validatePasswordStrength(boundaryPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(AT_LEAST + SecurityConstants.MIN_PASSWORD_LENGTH);
    }

    @Test
    void testValidatePasswordStrengthTooShort() {
        assertThatThrownBy(() -> userService.validatePasswordStrength(TestFixtures.SHORT_PASSWORD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(AT_LEAST + SecurityConstants.MIN_PASSWORD_LENGTH);
    }

    @Test
    void testPrepareUserForSaveNewUserWithEmptyPassword() {
        final User user = dataManager.create(User.class);
        user.setUsername(TestFixtures.uniqueUsername());

        assertThatThrownBy(() -> userService.prepareUserForSave(user, "", true))
                .isInstanceOf(SafeIllegalArgumentException.class)
                .hasMessageContaining("Password is required for new users");
    }

    @Test
    void testPrepareUserForSaveNewUserWithNullPassword() {
        final User user = dataManager.create(User.class);
        user.setUsername(TestFixtures.uniqueUsername());

        assertThatThrownBy(() -> userService.prepareUserForSave(user, null, true))
                .isInstanceOf(SafeIllegalArgumentException.class)
                .hasMessageContaining("Password is required for new users");
    }

    @Test
    void testPrepareUserForSaveNewUserWithShortPassword() {
        final User user = dataManager.create(User.class);
        user.setUsername(TestFixtures.uniqueUsername());

        assertThatThrownBy(() -> userService.prepareUserForSave(user, TestFixtures.SHORT_PASSWORD, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(AT_LEAST + SecurityConstants.MIN_PASSWORD_LENGTH);
    }

    @Test
    void testPrepareUserForSaveExistingUserWithShortPassword() {
        final User user = dataManager.create(User.class);
        user.setUsername(TestFixtures.uniqueUsername());
        user.setPassword(EXISTING_ENCODED_PASSWORD);
        savedUser = dataManager.save(user);

        assertThatThrownBy(() -> userService.prepareUserForSave(user, TestFixtures.SHORT_PASSWORD, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(AT_LEAST + SecurityConstants.MIN_PASSWORD_LENGTH);
    }

    @Test
    void testPrepareUserForSaveExistingUserWithValidPassword() {
        final User user = dataManager.create(User.class);
        user.setUsername(TestFixtures.uniqueUsername());
        user.setPassword(EXISTING_ENCODED_PASSWORD);
        savedUser = dataManager.save(user);

        final String newPassword = TestFixtures.VALID_PASSWORD;
        userService.prepareUserForSave(user, newPassword, false);

        assertThat(user.getPassword()).isNotBlank().isNotEqualTo(newPassword).isNotEqualTo(EXISTING_ENCODED_PASSWORD);
    }

    @AfterEach
    void afterEach() {
        if (savedUser != null) {
            dataManager.remove(savedUser);
            savedUser = null; // NOPMD - NullAssignment: prevents accidental reuse of removed entity
        }
    }
}
