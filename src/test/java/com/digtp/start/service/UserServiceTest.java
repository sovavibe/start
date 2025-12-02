package com.digtp.start.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.digtp.start.config.SecurityConstants;
import com.digtp.start.entity.User;
import com.digtp.start.test_support.AbstractIntegrationTest;
import com.digtp.start.test_support.AuthenticatedAsAdmin;
import io.jmix.core.DataManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith({SpringExtension.class, AuthenticatedAsAdmin.class})
class UserServiceTest extends AbstractIntegrationTest {

    @Autowired
    UserService userService;

    @Autowired
    DataManager dataManager;

    User savedUser;

    @Test
    void testEncodePassword() {
        final String plainPassword = "test-password-123";
        final String encodedPassword = userService.encodePassword(plainPassword);

        assertThat(encodedPassword).isNotBlank().isNotEqualTo(plainPassword);
        assertThat(encodedPassword.length()).isGreaterThan(plainPassword.length());
    }

    @Test
    @SuppressWarnings("nullness:argument") // Intentionally testing null edge case - Objects.equals handles null
    void testValidatePasswordConfirmation() {
        assertThat(userService.validatePasswordConfirmation("password", "password"))
                .isTrue();
        assertThat(userService.validatePasswordConfirmation("password", "different"))
                .isFalse();
        // Test null confirmPassword - Objects.equals handles null correctly
        assertThat(userService.validatePasswordConfirmation("password", null)).isFalse();
    }

    @Test
    void testPrepareUserForSaveNewUser() {
        final User user = dataManager.create(User.class);
        user.setUsername("test-user-" + System.currentTimeMillis());
        final String password = "test-password";

        userService.prepareUserForSave(user, password, true);

        assertThat(user.getPassword()).isNotBlank().isNotEqualTo(password);
        savedUser = dataManager.save(user);
    }

    @Test
    void testPrepareUserForSaveExistingUser() {
        final User user = dataManager.create(User.class);
        user.setUsername("test-user-" + System.currentTimeMillis());
        user.setPassword("existing-encoded-password");
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
                .hasMessageContaining("at least " + SecurityConstants.MIN_PASSWORD_LENGTH);
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
    void tearDown() {
        if (savedUser != null) {
            dataManager.remove(savedUser);
            savedUser = null; // NOPMD - NullAssignment: prevents accidental reuse of removed entity
        }
    }
}
