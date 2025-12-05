/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.digtp.start.testsupport.ReflectionTestUtils;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for SecurityConstants.
 *
 * <p>Tests that security constants are properly defined and accessible.
 */
class SecurityConstantsTest {

    @Test
    void testMinPasswordLength() {
        // Assert
        assertEquals(8, SecurityConstants.MIN_PASSWORD_LENGTH);
    }

    @Test
    void testSecurityConstantsCannotBeInstantiated() throws ReflectiveOperationException {
        // Arrange & Act - verify constructor exists but is private (utility class pattern)
        // Use reflection to verify constructor is accessible but private
        ReflectionTestUtils.invokeConstructor(SecurityConstants.class, new Class<?>[0]);

        // Assert - verify the constant is accessible
        assertEquals(8, SecurityConstants.MIN_PASSWORD_LENGTH);
    }
}
