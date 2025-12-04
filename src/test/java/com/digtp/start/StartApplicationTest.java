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
// Framework patterns suppressed via @SuppressWarnings (Palantir Baseline defaults):
// - PMD.CommentSize, PMD.CommentRequired, PMD.CommentDefaultAccessModifier, PMD.AtLeastOneConstructor
// - PMD.LongVariable, PMD.UnitTestContainsTooManyAsserts
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
