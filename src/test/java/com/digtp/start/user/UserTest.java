package com.digtp.start.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.digtp.start.entity.User;
import com.digtp.start.test_support.AbstractIntegrationTest;
import com.digtp.start.test_support.AuthenticatedAsAdmin;
import io.jmix.core.DataManager;
import io.jmix.core.security.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Sample integration test for the User entity.
 */
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith({SpringExtension.class, AuthenticatedAsAdmin.class})
class UserTest extends AbstractIntegrationTest {

    @Autowired
    DataManager dataManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    User savedUser;

    @Test
    void testSaveAndLoad() {
        // Create and save a new User
        final User user = dataManager.create(User.class);
        user.setUsername("test-user-" + System.currentTimeMillis());
        user.setPassword(passwordEncoder.encode("test-passwd"));
        savedUser = dataManager.save(user);

        // Check the new user can be loaded
        final User loadedUser = dataManager.load(User.class).id(user.getId()).one();
        assertThat(loadedUser).isEqualTo(user);

        // Check the new user is available through UserRepository
        final UserDetails userDetails = userRepository.loadUserByUsername(user.getUsername());
        assertThat(userDetails).isEqualTo(user);
    }

    @AfterEach
    void tearDown() {
        if (savedUser != null) {
            dataManager.remove(savedUser);
        }
    }
}
