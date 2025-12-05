/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.digtp.start.entity.User;
import com.digtp.start.testsupport.AbstractIntegrationTest;
import com.digtp.start.testsupport.AuthenticatedAsAdmin;
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

/**
 * Sample integration test for the User entity.
 */
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(AuthenticatedAsAdmin.class)
// Test: Test methods may have similar structure but test different scenarios
@SuppressWarnings({
    // Test: Some tests are clearer as separate methods rather than parameterized
    "java:S5976",
    // Test: Multiple assertions on same object are acceptable in tests for clarity
    "java:S5853",
    // Test: Test methods may have similar structure but test different scenarios
    "java:S4144"
})
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
        // Arrange
        final User user = dataManager.create(User.class);
        user.setUsername("test-user-" + System.currentTimeMillis());
        user.setPassword(passwordEncoder.encode("test-passwd"));

        // Act
        savedUser = dataManager.save(user);
        final User loadedUser = dataManager.load(User.class).id(user.getId()).one();
        final UserDetails userDetails = userRepository.loadUserByUsername(user.getUsername());

        // Assert
        assertThat(loadedUser).isEqualTo(user);
        assertThat(userDetails).isEqualTo(user);
    }

    @AfterEach
    void afterEach() {
        if (savedUser != null) {
            dataManager.remove(savedUser);
        }
    }
}
