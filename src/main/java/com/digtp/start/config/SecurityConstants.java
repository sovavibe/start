package com.digtp.start.config;

/**
 * Security-related constants used across the application.
 *
 * <p>Centralizes security configuration values to ensure consistency
 * and ease of maintenance.
 */
public final class SecurityConstants {

    /**
     * Minimum password length requirement.
     *
     * <p>Passwords must be at least this many characters long
     * to meet security requirements.
     */
    public static final int MIN_PASSWORD_LENGTH = 8;

    private SecurityConstants() {
        // Utility class - prevent instantiation
    }
}
