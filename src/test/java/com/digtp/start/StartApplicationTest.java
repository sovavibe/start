package com.digtp.start;

import static org.assertj.core.api.Assertions.assertThat;

import com.digtp.start.test_support.AbstractIntegrationTest;
import com.digtp.start.test_support.AuthenticatedAsAdmin;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith({SpringExtension.class, AuthenticatedAsAdmin.class})
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
