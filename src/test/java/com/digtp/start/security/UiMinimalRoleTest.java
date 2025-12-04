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
// Framework patterns suppressed via @SuppressWarnings (Palantir Baseline defaults):
// - PMD.CommentRequired, PMD.CommentDefaultAccessModifier, PMD.AtLeastOneConstructor
// - PMD.UnitTestAssertionsShouldIncludeMessage, PMD.AvoidDuplicateLiterals, PMD.UnitTestContainsTooManyAsserts
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
