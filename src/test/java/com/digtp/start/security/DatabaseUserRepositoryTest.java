/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.digtp.start.entity.User;
import com.digtp.start.testsupport.AbstractIntegrationTest;
import com.digtp.start.testsupport.AuthenticatedAsAdmin;
import io.jmix.core.security.SystemAuthenticator;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.ActiveProfiles;

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
class DatabaseUserRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private DatabaseUserRepository userRepository;

    @Autowired
    private SystemAuthenticator systemAuthenticator;

    @Test
    void testGetUserClass() {
        final Class<?> userClass = userRepository.getUserClass();

        assertThat(userClass).isEqualTo(User.class);
    }

    @Test
    void testGetSystemUser() {
        systemAuthenticator.begin();

        try {
            final User systemUser = userRepository.getSystemUser();

            assertThat(systemUser).isNotNull();
            assertThat(systemUser.getUsername()).isEqualTo("system");

            final Collection<? extends GrantedAuthority> authorities = systemUser.getAuthorities();
            assertThat(authorities).isNotEmpty();
            assertThat(authorities.stream()
                            .map(GrantedAuthority::getAuthority)
                            .anyMatch(auth -> auth.contains(FullAccessRole.CODE)))
                    .isTrue();
        } finally {
            systemAuthenticator.end();
        }
    }

    @Test
    void testGetAnonymousUser() {
        final User anonymousUser = userRepository.getAnonymousUser();

        assertThat(anonymousUser).isNotNull();
        assertThat(anonymousUser.getUsername()).isEqualTo("anonymous");
    }
}
