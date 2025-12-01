package com.digtp.start.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.digtp.start.entity.User;
import com.digtp.start.test_support.AuthenticatedAsAdmin;
import io.jmix.core.DataManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith({SpringExtension.class, AuthenticatedAsAdmin.class})
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    DataManager dataManager;

    User savedUser;

    @Test
    void testEncodePassword() {
        final String plainPassword = "test-password-123";
        final String encodedPassword = userService.encodePassword(plainPassword);

        assertThat(encodedPassword).isNotBlank();
        assertThat(encodedPassword).isNotEqualTo(plainPassword);
        assertThat(encodedPassword.length()).isGreaterThan(plainPassword.length());
    }

    @Test
    void testValidatePasswordConfirmation() {
        assertThat(userService.validatePasswordConfirmation("password", "password"))
                .isTrue();
        assertThat(userService.validatePasswordConfirmation("password", "different"))
                .isFalse();
        assertThat(userService.validatePasswordConfirmation("password", null)).isFalse();
    }

    @Test
    void testPrepareUserForSaveNewUser() {
        final User user = dataManager.create(User.class);
        user.setUsername("test-user-" + System.currentTimeMillis());
        final String password = "test-password";

        userService.prepareUserForSave(user, password, true);

        assertThat(user.getPassword()).isNotBlank();
        assertThat(user.getPassword()).isNotEqualTo(password);
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

    @AfterEach
    void tearDown() {
        if (savedUser != null) {
            dataManager.remove(savedUser);
            savedUser = null;
        }
    }
}
