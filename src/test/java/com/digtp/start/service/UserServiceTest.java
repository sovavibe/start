/*
 * (c) Copyright 2025 Digital Technologies and Platforms LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.digtp.start.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.digtp.start.config.SecurityConstants;
import com.digtp.start.entity.User;
import com.digtp.start.testsupport.AbstractIntegrationTest;
import com.digtp.start.testsupport.AuthenticatedAsAdmin;
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
// - PMD.CommentSize, PMD.CommentRequired, PMD.CommentDefaultAccessModifier, PMD.AtLeastOneConstructor
// - PMD.LongVariable, PMD.UnitTestContainsTooManyAsserts, PMD.UnitTestAssertionsShouldIncludeMessage
// - PMD.LawOfDemeter, PMD.TooManyMethods
@SuppressWarnings("PMD.AvoidDuplicateLiterals") // Test data uses duplicate string literals for clarity
class UserServiceTest extends AbstractIntegrationTest {

    private static final String TEST_USER_PREFIX = "test-user-";
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
        final String plainPassword = "test-password-123";

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
        user.setUsername(TEST_USER_PREFIX + System.currentTimeMillis());
        final String password = "test-password";

        // Act
        userService.prepareUserForSave(user, password, true);

        // Assert
        assertThat(user.getPassword()).isNotBlank().isNotEqualTo(password);
        savedUser = dataManager.save(user);
    }

    @Test
    void testPrepareUserForSaveExistingUser() {
        final User user = dataManager.create(User.class);
        user.setUsername(TEST_USER_PREFIX + System.currentTimeMillis());
        user.setPassword(EXISTING_ENCODED_PASSWORD);
        savedUser = dataManager.save(user);

        final String originalPassword = user.getPassword();
        userService.prepareUserForSave(user, null, false);

        assertThat(user.getPassword()).isEqualTo(originalPassword);
    }

    @Test
    void testValidatePasswordStrengthValid() {
        final String validPassword = "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH);
        // Should not throw exception
        assertThatCode(() -> userService.validatePasswordStrength(validPassword))
                .doesNotThrowAnyException();
    }

    @Test
    void testValidatePasswordStrengthBoundary() {
        final String boundaryPassword = "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH - 1);
        assertThatThrownBy(() -> userService.validatePasswordStrength(boundaryPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(AT_LEAST + SecurityConstants.MIN_PASSWORD_LENGTH);
    }

    @Test
    void testValidatePasswordStrengthTooShort() {
        assertThatThrownBy(() -> userService.validatePasswordStrength("short"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least " + SecurityConstants.MIN_PASSWORD_LENGTH);
    }

    @Test
    void testPrepareUserForSaveNewUserWithEmptyPassword() {
        final User user = dataManager.create(User.class);
        user.setUsername("test-user-" + System.currentTimeMillis());

        assertThatThrownBy(() -> userService.prepareUserForSave(user, "", true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password is required for new users");
    }

    @Test
    void testPrepareUserForSaveNewUserWithNullPassword() {
        final User user = dataManager.create(User.class);
        user.setUsername("test-user-" + System.currentTimeMillis());

        assertThatThrownBy(() -> userService.prepareUserForSave(user, null, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password is required for new users");
    }

    @Test
    void testPrepareUserForSaveNewUserWithShortPassword() {
        final User user = dataManager.create(User.class);
        user.setUsername("test-user-" + System.currentTimeMillis());

        assertThatThrownBy(() -> userService.prepareUserForSave(user, "short", true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least " + SecurityConstants.MIN_PASSWORD_LENGTH);
    }

    @Test
    void testPrepareUserForSaveExistingUserWithShortPassword() {
        final User user = dataManager.create(User.class);
        user.setUsername("test-user-" + System.currentTimeMillis());
        user.setPassword("existing-encoded-password");
        savedUser = dataManager.save(user);

        assertThatThrownBy(() -> userService.prepareUserForSave(user, "short", false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least " + SecurityConstants.MIN_PASSWORD_LENGTH);
    }

    @Test
    void testPrepareUserForSaveExistingUserWithValidPassword() {
        final User user = dataManager.create(User.class);
        user.setUsername("test-user-" + System.currentTimeMillis());
        user.setPassword("existing-encoded-password");
        savedUser = dataManager.save(user);

        final String newPassword = "newValidPassword123";
        userService.prepareUserForSave(user, newPassword, false);

        assertThat(user.getPassword()).isNotBlank().isNotEqualTo(newPassword).isNotEqualTo("existing-encoded-password");
    }

    @AfterEach
    void afterEach() {
        if (savedUser != null) {
            dataManager.remove(savedUser);
            savedUser = null; // NOPMD - NullAssignment: prevents accidental reuse of removed entity
        }
    }
}
