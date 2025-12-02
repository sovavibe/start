package com.digtp.start.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for FullAccessRole.
 *
 * <p>Tests that the role constant is properly defined.
 */
class FullAccessRoleTest {

    @Test
    void testFullAccessRoleCode() {
        // Assert
        assertNotNull(FullAccessRole.CODE);
        assertEquals("system-full-access", FullAccessRole.CODE);
    }

    @Test
    void testFullAccessRoleInterface() {
        // Assert - interface should be accessible
        assertNotNull(FullAccessRole.class);
        assertEquals("interface", FullAccessRole.class.isInterface() ? "interface" : "class");
    }
}
