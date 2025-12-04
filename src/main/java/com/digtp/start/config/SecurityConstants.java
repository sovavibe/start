/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.config;

/**
 * Security-related constants and utility methods used across the application.
 *
 * <p>Centralizes security configuration values and common validation patterns
 * to ensure consistency and ease of maintenance.
 */
// Framework patterns suppressed via @SuppressWarnings (Palantir Baseline defaults):
// - PMD.LongVariable
public final class SecurityConstants {

    /**
     * Minimum password length requirement.
     *
     * <p>Passwords must be at least this many characters long
     * to meet security requirements.
     */
    public static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * Checks if a password string is null or empty.
     *
     * <p>Utility method to centralize password null/empty checks
     * and avoid code duplication.
     *
     * @param password password string to check, may be null
     * @return true if password is null or empty, false otherwise
     */
    public static boolean isNullOrEmpty(final String password) {
        return password == null || password.isEmpty();
    }

    /**
     * Checks if a password string is not null and not empty.
     *
     * <p>Utility method to centralize password validation checks
     * and avoid code duplication.
     *
     * @param password password string to check, may be null
     * @return true if password is not null and not empty, false otherwise
     */
    public static boolean isNotNullOrEmpty(final String password) {
        return !isNullOrEmpty(password);
    }

    private SecurityConstants() {
        // Utility class - prevent instantiation
    }
}
