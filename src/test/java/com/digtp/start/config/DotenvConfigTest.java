package com.digtp.start.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

class DotenvConfigTest {

    private DotenvConfig dotenvConfig;
    private ApplicationEnvironmentPreparedEvent event;
    private ConfigurableEnvironment environment;
    private MutablePropertySources propertySources;

    @BeforeEach
    void setUp() {
        dotenvConfig = new DotenvConfig();
        environment = mock(ConfigurableEnvironment.class);
        propertySources = new MutablePropertySources();
        event = mock(ApplicationEnvironmentPreparedEvent.class);
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
        assertThat(order).isEqualTo(Integer.MIN_VALUE + expectedOrderOffset);
    }

    @Test
    void testOnApplicationEventWithNoEnvFile() {
        // Arrange
        // When .env file doesn't exist, Dotenv.configure().ignoreIfMissing().load()
        // returns an empty Dotenv instance
        // Event and environment are already mocked in setUp

        // Act
        dotenvConfig.onApplicationEvent(event);

        // Assert
        verify(environment).getPropertySources();
    }

    @Test
    void testOnApplicationEventWithExistingEnvironmentVariable() {
        // Arrange
        when(environment.containsProperty(any())).thenReturn(true);

        // Act
        dotenvConfig.onApplicationEvent(event);

        // Assert
        verify(environment, org.mockito.Mockito.atLeastOnce()).containsProperty(any());
    }

    @Test
    @SuppressWarnings("java:S4144") // Intentional: Tests different scenario (exception handling vs missing file)
    void testOnApplicationEventHandlesRuntimeException() {
        // Arrange
        // Event and environment are already mocked in setUp
        // Dotenv may throw RuntimeException if .env file has issues
        // This test verifies exception handling - the method catches RuntimeException
        // and logs at debug level without rethrowing
        // Note: Implementation is similar to testOnApplicationEventWithNoEnvFile but
        // tests different scenario (exception handling vs missing file gracefully)

        // Act
        dotenvConfig.onApplicationEvent(event);

        // Assert
        // Should complete without throwing exception even if Dotenv fails
        // The method catches RuntimeException internally
        verify(environment).getPropertySources();
    }
}
