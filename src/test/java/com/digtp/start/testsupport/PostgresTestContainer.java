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
 * <p>The container is automatically started when first accessed and
 * stopped when JVM exits (via Ryuk).
 */
// Framework patterns suppressed via @SuppressWarnings (Palantir Baseline defaults):
// - PMD.CommentSize, PMD.CommentRequired, PMD.CommentDefaultAccessModifier, PMD.AtLeastOneConstructor
public final class PostgresTestContainer {

    private static final PostgreSQLContainer<?> INSTANCE = new PostgreSQLContainer<>(
                    DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true)
            .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*\\n", 1))
            .withStartupTimeout(Duration.ofSeconds(30));

    static {
        INSTANCE.start();
    }

    private PostgresTestContainer() {
        // Utility class
    }

    public static PostgreSQLContainer<?> getInstance() {
        return INSTANCE;
    }

    public static String getJdbcUrl() {
        return INSTANCE.getJdbcUrl();
    }

    public static String getUsername() {
        return INSTANCE.getUsername();
    }

    public static String getPassword() {
        return INSTANCE.getPassword();
    }
}
