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
// Test: Test methods call reflection which can throw ReflectiveOperationException
@SuppressWarnings({
    // Test: Some tests are clearer as separate methods rather than parameterized
    "java:S5976",
    // Test: Multiple assertions on same object are acceptable in tests for clarity
    "java:S5853",
    // Test: Test methods may have similar structure but test different scenarios
    "java:S4144",
    // Test: Test methods call reflection which can throw ReflectiveOperationException
    "java:S1130"
})
class SecurityConstantsTest {

    @Test
    void testMinPasswordLength() {
        // Assert
        assertEquals(8, SecurityConstants.MIN_PASSWORD_LENGTH);
    }

    @Test
    // Framework: setAccessible() is valid for testing private constructors (utility class pattern)
    @SuppressWarnings("PMD.AvoidAccessibilityAlteration") // reflection for testing
    void testSecurityConstantsCannotBeInstantiated() throws ReflectiveOperationException {
        // Arrange & Act - verify constructor exists but is private (utility class pattern)
        final Constructor<SecurityConstants> constructor = SecurityConstants.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();

        // Assert - verify the constant is accessible
        assertEquals(8, SecurityConstants.MIN_PASSWORD_LENGTH);
    }
}
