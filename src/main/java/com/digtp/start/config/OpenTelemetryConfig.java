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
// - PMD.CommentSize, PMD.AtLeastOneConstructor
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
