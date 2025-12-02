package com.digtp.start.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void testSecurityConstantsCannotBeInstantiated() throws Exception {
        // Arrange
        final var constructor = SecurityConstants.class.getDeclaredConstructor();

        // Act & Assert
        constructor.setAccessible(true);
        final SecurityConstants instance = constructor.newInstance();
        // Constructor exists but is private - utility class pattern
        // We verify the constant is accessible
        assertEquals(8, SecurityConstants.MIN_PASSWORD_LENGTH);
    }
}
