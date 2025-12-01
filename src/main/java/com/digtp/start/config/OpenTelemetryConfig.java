package com.digtp.start.config;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenTelemetry configuration for centralized logging.
 *
 * <p>Provides OpenTelemetry instance for Logback appender.
 * Spring Boot Actuator configures OpenTelemetry SDK automatically
 * when management.otlp.logging.endpoint is set.
 */
@Configuration
@ConditionalOnProperty(name = "management.otlp.logging.endpoint")
@Slf4j
public final class OpenTelemetryConfig {

    @Bean
    public OpenTelemetry openTelemetry() {
        // GlobalOpenTelemetry is configured by Spring Boot Actuator
        // when management.otlp.logging.endpoint is set
        final OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();
        log.info("OpenTelemetry initialized for centralized logging");
        return openTelemetry;
    }
}
