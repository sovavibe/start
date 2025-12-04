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
// - PMD.CommentSize, PMD.AtLeastOneConstructor
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
