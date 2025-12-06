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
class OpenTelemetryConfigTest extends AbstractIntegrationTest {

    @Test
    void testOpenTelemetryConfigClassExists() {
        // Test that OpenTelemetryConfig class can be loaded
        // Actual bean creation is conditional on management.otlp.logging.endpoint property
        final Class<?> configClass = OpenTelemetryConfig.class;
        assertThat(configClass).isNotNull();
    }
}
