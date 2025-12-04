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
package com.digtp.start.config;

/**
 * Security-related constants and utility methods used across the application.
 *
 * <p>Centralizes security configuration values and common validation patterns
 * to ensure consistency and ease of maintenance.
 */
// Framework patterns suppressed via @SuppressWarnings (Palantir Baseline defaults):
// - PMD.CommentSize, PMD.LongVariable
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
