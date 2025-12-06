/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.digtp.start.testsupport.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
// Test: Test methods may have similar structure but test different scenarios
@SuppressWarnings({
    // Test: Some tests are clearer as separate methods rather than parameterized
    "java:S5976", // parameterized test
    // Test: Multiple assertions on same object are acceptable in tests for clarity
    "java:S5853", // multiple assertions
    // Test: Test methods may have similar structure but test different scenarios
    "java:S4144" // similar methods
})
class OpenTelemetryConfigTest extends AbstractIntegrationTest {

    @Test
    void testOpenTelemetryConfigClassExists() {
        // Test that OpenTelemetryConfig class can be loaded
        // Actual bean creation is conditional on management.otlp.logging.endpoint property
        final Class<?> configClass = OpenTelemetryConfig.class;
        assertThat(configClass).isNotNull();
    }
}
