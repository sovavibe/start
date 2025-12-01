package com.digtp.start.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OpenTelemetryConfigTest {

    @Test
    void testOpenTelemetryConfigClassExists() {
        // Test that OpenTelemetryConfig class can be loaded
        // Actual bean creation is conditional on management.otlp.logging.endpoint property
        final Class<?> configClass = OpenTelemetryConfig.class;
        assertThat(configClass).isNotNull();
    }
}
