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
package com.digtp.start.service;

// TODO: Replace package name with your package

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.digtp.start.entity.YourEntity; // TODO: Replace with your entity
import com.digtp.start.testsupport.AbstractIntegrationTest;
import com.digtp.start.testsupport.AuthenticatedAsAdmin;
import io.jmix.core.DataManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Integration tests for [YourEntityService].
 *
 * <p>Follows AAA/GWT pattern (Arrange-Act-Assert / Given-When-Then).
 * Uses AssertJ for fluent assertions.
 *
 * <p>Best Practices:
 * <ul>
 *   <li>@SpringBootTest for integration tests</li>
 *   <li>@ExtendWith for test extensions (SpringExtension, AuthenticatedAsAdmin)</li>
 *   <li>@ActiveProfiles("test") for test configuration</li>
 *   <li>@Autowired allowed in tests (not in production code)</li>
 *   <li>@AfterEach for cleanup (remove test data)</li>
 *   <li>AAA/GWT pattern: Arrange-Act-Assert / Given-When-Then</li>
 *   <li>Unique test data (use timestamps, UUIDs)</li>
 *   <li>Test naming: test&lt;MethodUnderTest&gt;_&lt;state&gt;</li>
 *   <li>Never delete failing tests - fix root cause</li>
 * </ul>
 *
 * <p>Reference: See UserServiceTest.java, StartPasswordValidatorTest.java for complete examples
 */
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith({SpringExtension.class, AuthenticatedAsAdmin.class})
class YourEntityServiceTest extends AbstractIntegrationTest {

    // TODO: Define test constants
    private static final String TEST_PREFIX = "test-";

    @Autowired
    YourEntityService yourEntityService; // TODO: Replace with your service

    @Autowired
    DataManager dataManager;

    // TODO: Store test data for cleanup
    YourEntity savedEntity;

    @Test
    void testCreate() {
        // Arrange (Given)
        final YourEntity entity = dataManager.create(YourEntity.class);
        // TODO: Set entity properties
        // Example:
        // entity.setName(TEST_PREFIX + System.currentTimeMillis());

        // Act (When)
        savedEntity = yourEntityService.create(entity);

        // Assert (Then)
        assertThat(savedEntity).isNotNull();
        assertThat(savedEntity.getId()).isNotNull();
        // TODO: Add more assertions
    }

    @Test
    void testCreateWithInvalidData() {
        // Arrange
        final YourEntity entity = dataManager.create(YourEntity.class);
        // TODO: Set invalid data
        // Example:
        // entity.setName(null); // Invalid

        // Act & Assert
        assertThatThrownBy(() -> yourEntityService.create(entity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("required"); // TODO: Adjust message
    }

    @Test
    void testFindById() {
        // Arrange
        final YourEntity entity = dataManager.create(YourEntity.class);
        // TODO: Set entity properties
        savedEntity = dataManager.save(entity);

        // Act
        final YourEntity found = yourEntityService.findById(savedEntity.getId());

        // Assert
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(savedEntity.getId());
        // TODO: Add more assertions
    }

    @Test
    void testFindByIdNotFound() {
        // Arrange
        final Object nonExistentId = java.util.UUID.randomUUID();

        // Act
        final YourEntity found = yourEntityService.findById(nonExistentId);

        // Assert
        assertThat(found).isNull();
    }

    @Test
    void testUpdate() {
        // Arrange
        final YourEntity entity = dataManager.create(YourEntity.class);
        // TODO: Set entity properties
        savedEntity = dataManager.save(entity);
        // TODO: Modify entity
        // Example:
        // savedEntity.setName("updated-name");

        // Act
        final YourEntity updated = yourEntityService.update(savedEntity);

        // Assert
        assertThat(updated).isNotNull();
        // TODO: Verify updates
    }

    /**
     * Cleanup: Remove test data after each test.
     *
     * <p>Uses @AfterEach to ensure test data is cleaned up.
     * Prevents test pollution and database bloat.
     */
    @AfterEach
    void afterEach() {
        if (savedEntity != null) {
            dataManager.remove(savedEntity);
            savedEntity = null;
        }
    }
}

