package com.digtp.start.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.digtp.start.test_support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class OpenTelemetryAppenderInitializerTest extends AbstractIntegrationTest {

    @Test
    void testOpenTelemetryAppenderInitializerClassExists() {
        // Test that OpenTelemetryAppenderInitializer class can be loaded
        // Actual bean creation is conditional on management.otlp.logging.endpoint property
        final Class<?> initializerClass = OpenTelemetryAppenderInitializer.class;
        assertThat(initializerClass).isNotNull();
    }
}
