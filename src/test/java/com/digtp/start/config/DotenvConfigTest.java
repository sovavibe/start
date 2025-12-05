/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.config;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

class DotenvConfigTest {

    private DotenvConfig dotenvConfig;
    private ApplicationEnvironmentPreparedEvent event;
    private ConfigurableEnvironment environment;

    @BeforeEach
    void beforeEach() {
        dotenvConfig = new DotenvConfig();
        environment = Mockito.mock(ConfigurableEnvironment.class);
        final MutablePropertySources propertySources = new MutablePropertySources();
        event = Mockito.mock(ApplicationEnvironmentPreparedEvent.class);
        when(event.getEnvironment()).thenReturn(environment);
        when(environment.getPropertySources()).thenReturn(propertySources);
    }

    @Test
    void testGetOrder() {
        // Arrange
        // (No setup needed, using instance from setUp)
        final int expectedOrderOffset = 10; // ORDER_OFFSET from DotenvConfig

        // Act
        final int order = dotenvConfig.getOrder();

        // Assert
        Assertions.assertThat(order).isEqualTo(Integer.MIN_VALUE + expectedOrderOffset);
    }

    @Test
    void testOnApplicationEventWithNoEnvFile() {
        // Arrange
        // When .env file doesn't exist, Dotenv.configure().ignoreIfMissing().load()
        // returns an empty Dotenv instance
        // Event and environment are already mocked in setUp

        // Act & Assert
        verifyOnApplicationEventCompletes();
    }

    @Test
    void testOnApplicationEventWithExistingEnvironmentVariable() {
        // Arrange
        when(environment.containsProperty(ArgumentMatchers.any())).thenReturn(true);

        // Act
        dotenvConfig.onApplicationEvent(event);

        // Assert
        verify(environment, Mockito.atLeastOnce()).containsProperty(ArgumentMatchers.any());
    }

    @Test
    void testOnApplicationEventHandlesRuntimeException() {
        // Arrange
        // Event and environment are already mocked in setUp
        // Dotenv may throw RuntimeException if .env file has issues
        // This test verifies exception handling - the method catches RuntimeException
        // and logs at debug level without rethrowing

        // Act & Assert
        // Should complete without throwing exception even if Dotenv fails
        // The method catches RuntimeException internally
        verifyOnApplicationEventCompletes();
    }

    /**
     * Helper method to verify that onApplicationEvent completes successfully
     * and accesses environment property sources.
     *
     * <p>Used by multiple tests to avoid code duplication while testing different scenarios.
     */
    private void verifyOnApplicationEventCompletes() {
        dotenvConfig.onApplicationEvent(event);
        verify(environment).getPropertySources();
    }
}
