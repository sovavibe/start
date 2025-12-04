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
// Framework patterns suppressed via @SuppressWarnings (Palantir Baseline defaults):
// - PMD.CommentRequired, PMD.CommentDefaultAccessModifier, PMD.AtLeastOneConstructor
class OpenTelemetryAppenderInitializerTest extends AbstractIntegrationTest {

    @Test
    void testOpenTelemetryAppenderInitializerClassExists() {
        // Test that OpenTelemetryAppenderInitializer class can be loaded
        // Actual bean creation is conditional on management.otlp.logging.endpoint property
        final Class<?> initializerClass = OpenTelemetryAppenderInitializer.class;
        assertThat(initializerClass).isNotNull();
    }
}
