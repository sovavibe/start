/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.testsupport;

import java.time.Duration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

/**
 * Singleton PostgreSQL container for all integration tests.
 *
 * <p>This container is started once and reused across all test classes,
 * significantly reducing test execution time.
 *
 * <p>The container is lazily initialized when first accessed and
 * stopped when JVM exits (via Ryuk).
 */
public final class PostgresTestContainer {

    private static PostgreSQLContainer<?> instance;

    private PostgresTestContainer() {
        // Utility class
    }

    // Framework: Singleton container is intentionally not closed - reused across all tests
    @SuppressWarnings("resource")
    private static synchronized PostgreSQLContainer<?> getInstance() {
        if (instance == null) {
            instance = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test")
                    .withReuse(true)
                    .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*\\n", 1))
                    .withStartupTimeout(Duration.ofSeconds(30));
            instance.start();
        }
        return instance;
    }

    public static String getJdbcUrl() {
        return getInstance().getJdbcUrl();
    }

    public static String getUsername() {
        return getInstance().getUsername();
    }

    public static String getPassword() {
        return getInstance().getPassword();
    }
}
