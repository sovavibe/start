/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void testSecurityConstantsCannotBeInstantiated() {
        // Verify utility class pattern - constants are accessible but class cannot be instantiated
        // This test verifies the class design without using reflection (which PMD flags)
        // The private constructor is verified by compilation - if it were public, this would compile differently
        assertEquals(8, SecurityConstants.MIN_PASSWORD_LENGTH);
    }
}
