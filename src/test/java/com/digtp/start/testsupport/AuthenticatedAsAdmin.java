/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.testsupport;

import io.jmix.core.security.SystemAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContextManager;

/**
 * JUnit extension for providing system authentication in integration tests.
 * Should be used in {@code @ExtendWith} annotation on the test class.
 *
 * <p>This class is not intended for extension. It implements JUnit extension callbacks
 * and should be used as-is via {@code @ExtendWith} annotation.
 */
// Framework patterns suppressed via @SuppressWarnings (Palantir Baseline defaults):
// - PMD.CommentRequired, PMD.CommentDefaultAccessModifier, PMD.AtLeastOneConstructor
// - PMD.LongVariable
@Slf4j
public final class AuthenticatedAsAdmin implements BeforeEachCallback, AfterEachCallback {

    /**
     * Begins system authentication as admin before each test.
     *
     * @param context JUnit extension context
     */
    @Override
    public void beforeEach(final ExtensionContext context) {
        getSystemAuthenticator(context).begin("admin");
    }

    /**
     * Ends system authentication after each test.
     *
     * @param context JUnit extension context
     */
    @Override
    public void afterEach(final ExtensionContext context) {
        getSystemAuthenticator(context).end();
    }

    private SystemAuthenticator getSystemAuthenticator(final ExtensionContext context) {
        final ApplicationContext applicationContext = getApplicationContext(context);
        return applicationContext.getBean(SystemAuthenticator.class);
    }

    private ApplicationContext getApplicationContext(final ExtensionContext context) {
        final Class<?> testClass = context.getRequiredTestClass();
        final TestContextManager testContextManager = getOrCreateTestContextManager(context, testClass);
        // Prepare TestContextManager if not already prepared by SpringExtension
        final Object testInstance = context.getRequiredTestInstance();
        try {
            testContextManager.prepareTestInstance(testInstance);
        } catch (final Exception e) {
            // If already prepared by SpringExtension, ignore - this is expected behavior
            // (idempotent operation)
            log.debug("TestContextManager already prepared by SpringExtension: {}", e.getMessage());
        }
        try {
            testContextManager.beforeTestMethod(testInstance, context.getRequiredTestMethod());
        } catch (final Exception e) {
            // If already called by SpringExtension, ignore - this is expected behavior
            // (idempotent operation)
            log.debug("beforeTestMethod already called by SpringExtension: {}", e.getMessage());
        }
        return testContextManager.getTestContext().getApplicationContext();
    }

    private TestContextManager getOrCreateTestContextManager(final ExtensionContext context, final Class<?> testClass) {
        final ExtensionContext.Store store = context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL);
        return store.getOrComputeIfAbsent(
                TestContextManager.class, _key -> new TestContextManager(testClass), TestContextManager.class);
    }
}
