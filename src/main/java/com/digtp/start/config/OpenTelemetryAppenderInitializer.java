/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Initializes OpenTelemetry Logback Appender for centralized logging.
 *
 * <p>The OpenTelemetryAppender requires access to an OpenTelemetry instance
 * to function properly. This instance must be set programmatically during application startup.
 *
 * <p>See also logback-spring.xml:
 * <pre>{@code
 * <appender name="OpenTelemetry"
 *     class="io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender" />
 * }</pre>
 *
 * <p>Enabled only when management.otlp.logging.endpoint is configured.
 */
@Component
@ConditionalOnProperty(name = "management.otlp.logging.endpoint")
@Slf4j
@RequiredArgsConstructor
// Framework patterns suppressed via @SuppressWarnings (Palantir Baseline defaults):
// - PMD.AtLeastOneConstructor
public final class OpenTelemetryAppenderInitializer implements InitializingBean {

    /**
     * OpenTelemetry instance for Logback appender initialization.
     */
    private final OpenTelemetry openTelemetry;

    @Override
    public void afterPropertiesSet() {
        OpenTelemetryAppender.install(openTelemetry);
        log.info("OpenTelemetry Logback Appender initialized");
    }
}
