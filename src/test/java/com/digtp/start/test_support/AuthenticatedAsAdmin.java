package com.digtp.start.test_support;

import io.jmix.core.security.SystemAuthenticator;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * JUnit extension for providing system authentication in integration tests.
 * Should be used in {@code @ExtendWith} annotation on the test class.
 *
 * <p>This class is not intended for extension. It implements JUnit extension callbacks
 * and should be used as-is via {@code @ExtendWith} annotation.
 */
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
        final ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);
        return applicationContext.getBean(SystemAuthenticator.class);
    }
}
