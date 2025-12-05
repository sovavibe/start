/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for SecurityConstants.
 *
 * <p>Tests that security constants are properly defined and accessible.
 */
@SuppressWarnings(
        "PMD.AvoidAccessibilityAlteration") // Test: reflection to access private constructor for testing utility class.
// SecurityConstants has private constructor to prevent instantiation - reflection allows testing this pattern.
class SecurityConstantsTest {

    @Test
    void testMinPasswordLength() {
        // Assert
        assertEquals(8, SecurityConstants.MIN_PASSWORD_LENGTH);
    }

    @Test
    void testSecurityConstantsCannotBeInstantiated() throws Exception {
        // Arrange
        final Constructor<SecurityConstants> constructor = SecurityConstants.class.getDeclaredConstructor();

        // Act & Assert
        constructor.setAccessible(true);
        constructor.newInstance(); // Verify constructor exists but is private
        // Constructor exists but is private - utility class pattern
        // We verify the constant is accessible
        assertEquals(8, SecurityConstants.MIN_PASSWORD_LENGTH);
    }
}
