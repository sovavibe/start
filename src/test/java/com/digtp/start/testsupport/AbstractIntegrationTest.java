/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.testsupport;

import io.jmix.flowui.testassist.UiTestUtils;
import io.jmix.flowui.view.View;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Base class for integration tests with PostgreSQL Testcontainers.
 *
 * <p>Uses singleton PostgreSQL container that is started once and reused
 * across all test classes, significantly reducing test execution time.
 *
 * <p>Container reuse is enabled via testcontainers.properties.
 * This allows containers to be reused between test runs, reducing
 * test execution time from 30s+ to 2-5s per test class.
 *
 * <p>Usage:
 * <pre>{@code
 * @SpringBootTest
 * @ActiveProfiles("test")
 * class MyTest extends AbstractIntegrationTest {
 *     // test methods
 * }
 * }</pre>
 */
@Slf4j
// Framework patterns suppressed via @SuppressWarnings (Palantir Baseline defaults):
// - PMD.CommentRequired, PMD.CommentDefaultAccessModifier, PMD.AtLeastOneConstructor
// - PMD.LongVariable
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class AbstractIntegrationTest {

    @DynamicPropertySource
    // PMD.GuardLogStatement suppressed via @SuppressWarnings (Palantir Baseline defaults)
    static void configureProperties(final DynamicPropertyRegistry registry) {
        // Use singleton container instance shared across all test classes
        registry.add("main.datasource.url", PostgresTestContainer::getJdbcUrl);
        registry.add("main.datasource.username", PostgresTestContainer::getUsername);
        registry.add("main.datasource.password", PostgresTestContainer::getPassword);
        log.debug("PostgreSQL test container configured: {}", PostgresTestContainer.getJdbcUrl());
    }

    /**
     * Gets current view as {@link View}.
     *
     * <p>Jmix's {@link UiTestUtils#getCurrentView()} returns {@code Object} due to
     * class loader issues in test environment. This helper method centralizes
     * the cast operation.
     *
     * @return current view as {@link View}
     */
    protected static View<?> getCurrentViewAsView() {
        return (View<?>) UiTestUtils.getCurrentView();
    }

    /**
     * Invokes a method on an object using reflection.
     *
     * <p>Helper method to invoke private/protected methods in tests using reflection
     * to avoid ClassCastException with Jmix class loaders. Uses type-safe cast via
     * {@code Class.cast()} to avoid unchecked warnings.
     *
     * @param <T> return type of the method
     * @param returnType class of the return type (for type-safe casting)
     * @param object object on which to invoke the method
     * @param methodName name of the method to invoke
     * @param paramTypes parameter types of the method
     * @param args arguments to pass to the method
     * @return result of method invocation
     * @throws ReflectiveOperationException if method cannot be found or invoked
     */
    @SuppressWarnings("PMD.AvoidAccessibilityAlteration") // Test: reflection to access private methods
    protected static <T> T invokeMethod(
            final Class<T> returnType,
            final Object object,
            final String methodName,
            final Class<?>[] paramTypes,
            final Object... args)
            throws ReflectiveOperationException {
        // Try getDeclaredMethod first (for private/protected methods), then getMethod (for public/inherited methods)
        Method method;
        try {
            method = object.getClass().getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
        } catch (NoSuchMethodException e) {
            method = object.getClass().getMethod(methodName, paramTypes);
        }
        final Object result = method.invoke(object, args);
        return returnType.cast(result);
    }
}
