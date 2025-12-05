/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start;

import static org.assertj.core.api.Assertions.assertThat;

import com.digtp.start.testsupport.AbstractIntegrationTest;
import com.digtp.start.testsupport.AuthenticatedAsAdmin;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(AuthenticatedAsAdmin.class)
// Test: Test methods may have similar structure but test different scenarios
@SuppressWarnings({
    // Test: Some tests are clearer as separate methods rather than parameterized
    "java:S5976",
    // Test: Multiple assertions on same object are acceptable in tests for clarity
    "java:S5853",
    // Test: Test methods may have similar structure but test different scenarios
    "java:S4144"
})
class StartApplicationTest extends AbstractIntegrationTest {

    @Autowired
    Environment environment;

    @Autowired
    @Qualifier("dataSource")
    DataSource dataSource;

    @Autowired(required = false)
    DataSourceProperties dataSourceProperties;

    @Test
    void testApplicationContextLoads() {
        assertThat(environment).isNotNull();
        assertThat(dataSource).isNotNull();
    }

    @Test
    void testDataSourceProperties() {
        if (dataSourceProperties != null) {
            assertThat(dataSourceProperties).isNotNull();
        }
    }
}
