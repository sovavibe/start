/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.testsupport;

import io.jmix.flowui.testassist.UiTestUtils;
import io.jmix.flowui.view.View;
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
// Framework: abstract base class for tests provides common methods (invokeMethod, etc.)
// No abstract methods but provides shared functionality. Standard pattern for test base classes
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class AbstractIntegrationTest {

    // Note: Use @BeforeEach annotation for test setup instead of setUp() method
    // This follows JUnit 5 best practices and satisfies Checkstyle rules

    @DynamicPropertySource
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
}
