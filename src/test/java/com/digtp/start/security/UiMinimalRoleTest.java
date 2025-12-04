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
// - PMD.CommentSize, PMD.CommentRequired, PMD.CommentDefaultAccessModifier, PMD.AtLeastOneConstructor
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
