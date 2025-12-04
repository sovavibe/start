/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Configuration for loading .env file support.
 *
 * <p>Loads environment variables from .env file in the project root directory
 * before Spring Boot loads application.properties. This ensures that variables
 * from .env are available during property resolution.
 *
 * <p>Variables from .env file are loaded into Spring Environment with lowest
 * precedence (environment variables and system properties take precedence).
 *
 * <p>The .env file should be placed in the project root directory and should
 * not be committed to version control (already in .gitignore).
 */
@Slf4j
public final class DotenvConfig implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {

    /**
     * Order offset for loading .env variables early, but after system properties.
     */
    private static final int ORDER_OFFSET = 10;

    @Override
    public void onApplicationEvent(final ApplicationEnvironmentPreparedEvent event) {
        try {
            final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

            // Extract environment to avoid PMD.LawOfDemeter violation
            final ConfigurableEnvironment environment = event.getEnvironment();
            final Map<String, Object> dotenvProperties = new HashMap<>();

            // Load .env variables into Spring Environment
            dotenv.entries().forEach(entry -> {
                final String key = entry.getKey();
                final String value = entry.getValue();

                // Only add if not already present (environment variables take precedence)
                if (!environment.containsProperty(key)) {
                    dotenvProperties.put(key, value);
                    log.debug("Loaded {} from .env file", key);
                }
            });

            if (dotenvProperties.isEmpty()) {
                log.debug("No .env file found or all variables already set");
                return;
            }

            environment.getPropertySources().addLast(new MapPropertySource("dotenv", dotenvProperties));
            log.info("Loaded {} environment variables from .env file", dotenvProperties.size());
        } catch (final RuntimeException exception) {
            // Dotenv library throws RuntimeException for various errors (file not found, parsing errors, etc.)
            // .env file is optional (ignoreIfMissing() is configured), so we log at debug level
            // Catching RuntimeException is necessary as Dotenv doesn't declare specific checked exceptions
            // and wraps various exceptions (IOException, parsing errors) as RuntimeException
            log.debug("No .env file found or error loading it: {}", exception.getMessage(), exception);
        }
    }

    @Override
    public int getOrder() {
        // Load early, but after system properties and environment variables
        return HIGHEST_PRECEDENCE + ORDER_OFFSET;
    }
}
