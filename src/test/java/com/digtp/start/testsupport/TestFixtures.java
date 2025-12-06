/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.testsupport;

import com.digtp.start.config.SecurityConstants;
import com.digtp.start.entity.User;
import java.util.UUID;

/**
 * Centralized test data fixtures.
 *
 * <p>Provides reusable test data to avoid hard-coded values in tests.
 * Reduces SpotBugs HARD_CODE_PASSWORD warnings and improves test maintainability.
 */
@SuppressWarnings({
    // Test: Test fixtures intentionally use repeated string literals for test data (usernames, passwords)
    "PMD.AvoidDuplicateLiterals", // duplicate literals
    // Test: Utility class for test data (TestFixtures), not a test class itself
    "PMD.TestClassWithoutTestCases" // no test cases
})
public final class TestFixtures {

    /**
     * Default test password that meets minimum length requirements.
     */
    public static final String DEFAULT_TEST_PASSWORD = "test-password-123";

    /**
     * Alternative test password for testing different scenarios.
     */
    public static final String ALTERNATIVE_TEST_PASSWORD = "test-passwd";

    /**
     * Valid password that meets minimum length requirements.
     */
    public static final String VALID_PASSWORD = "newValidPassword123";

    /**
     * Short password that does not meet minimum length requirements.
     */
    public static final String SHORT_PASSWORD = "short";

    /**
     * Test username prefix for generating unique usernames.
     */
    public static final String TEST_USER_PREFIX = "test-user-";

    /**
     * Default test username.
     */
    public static final String DEFAULT_TEST_USERNAME = "test-user";

    /**
     * Admin test username.
     */
    public static final String ADMIN_TEST_USERNAME = "admin";

    private TestFixtures() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates test credentials with default values.
     *
     * @return credentials with default test username and password
     */
    public static Credentials defaultCredentials() {
        return new Credentials(DEFAULT_TEST_USERNAME, DEFAULT_TEST_PASSWORD);
    }

    /**
     * Creates admin test credentials.
     *
     * @return credentials with admin username and default password
     */
    public static Credentials adminCredentials() {
        return new Credentials(ADMIN_TEST_USERNAME, DEFAULT_TEST_PASSWORD);
    }

    /**
     * Creates test credentials with custom username and password.
     *
     * @param username username for credentials
     * @param password password for credentials
     * @return credentials with specified username and password
     */
    public static Credentials credentials(final String username, final String password) {
        return new Credentials(username, password);
    }

    /**
     * Creates a new user entity with default test values.
     *
     * <p>User is not persisted - caller must save using DataManager if needed.
     *
     * @return new user entity with test username and active status
     */
    public static User newUser() {
        final User user = new User();
        user.setUsername(TEST_USER_PREFIX + System.currentTimeMillis());
        user.setEmail("test@example.com");
        user.setActive(true);
        return user;
    }

    /**
     * Creates a new user entity with custom username.
     *
     * <p>User is not persisted - caller must save using DataManager if needed.
     *
     * @param username username for the user
     * @return new user entity with specified username
     */
    public static User newUser(final String username) {
        final User user = newUser();
        user.setUsername(username);
        return user;
    }

    /**
     * Creates an existing user entity with ID and default test values.
     *
     * <p>User is not persisted - caller must save using DataManager if needed.
     *
     * @return user entity with generated ID and test username
     */
    public static User existingUser() {
        final User user = newUser();
        user.setId(UUID.randomUUID());
        return user;
    }

    /**
     * Creates a valid password that meets minimum length requirements.
     *
     * @return password string with minimum required length
     */
    public static String validPassword() {
        return "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH);
    }

    /**
     * Creates a password that is one character short of minimum length.
     *
     * @return password string that is too short
     */
    public static String shortPassword() {
        return "a".repeat(SecurityConstants.MIN_PASSWORD_LENGTH - 1);
    }

    /**
     * Generates a unique test username with timestamp.
     *
     * @return unique username with test prefix and timestamp
     */
    public static String uniqueUsername() {
        return TEST_USER_PREFIX + System.currentTimeMillis();
    }

    /**
     * Credentials record for username/password pairs.
     *
     * @param username username
     * @param password password
     */
    public record Credentials(String username, String password) {}
}
