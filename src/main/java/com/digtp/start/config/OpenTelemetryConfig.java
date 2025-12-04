/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
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
 *
 * <p>This class is not designed for extension. All methods are final
 * and should not be overridden.
 */
@Configuration
@ConditionalOnProperty(name = "management.otlp.logging.endpoint")
@Slf4j
// Framework patterns suppressed via @SuppressWarnings (Palantir Baseline defaults):
// - PMD.AtLeastOneConstructor
public class OpenTelemetryConfig {

    /**
     * Creates OpenTelemetry bean instance.
     *
     * <p>This method is not designed for extension. It returns the global
     * OpenTelemetry instance configured by Spring Boot Actuator.
     *
     * @return OpenTelemetry instance
     */
    @Bean
    public OpenTelemetry openTelemetry() {
        // GlobalOpenTelemetry is configured by Spring Boot Actuator
        // when management.otlp.logging.endpoint is set
        final OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();
        log.info("OpenTelemetry initialized for centralized logging");
        return openTelemetry;
    }
}
