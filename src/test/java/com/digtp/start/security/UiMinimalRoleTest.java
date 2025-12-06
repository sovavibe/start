/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for UiMinimalRole.
 *
 * <p>Tests that the role constant is properly defined.
 */
// Test: Test methods may have similar structure but test different scenarios
@SuppressWarnings({
    // Test: Some tests are clearer as separate methods rather than parameterized
    "java:S5976", // parameterized test
    // Test: Multiple assertions on same object are acceptable in tests for clarity
    "java:S5853", // multiple assertions
    // Test: Test methods may have similar structure but test different scenarios
    "java:S4144" // similar methods
})
class UiMinimalRoleTest {

    @Test
    void testUiMinimalRoleCode() {
        // Assert
        assertNotNull(UiMinimalRole.CODE);
        assertEquals("ui-minimal", UiMinimalRole.CODE);
    }

    @Test
    void testUiMinimalRoleInterface() {
        // Assert - interface should be accessible
        assertNotNull(UiMinimalRole.class);
        assertEquals("interface", UiMinimalRole.class.isInterface() ? "interface" : "class");
    }
}
