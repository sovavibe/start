package com.digtp.start.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.digtp.start.test_support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class StartSecurityConfigurationTest extends AbstractIntegrationTest {

    @Autowired(required = false)
    private StartSecurityConfiguration securityConfiguration;

    @Test
    void testSecurityConfigurationExists() {
        assertThat(securityConfiguration).isNotNull();
    }
}
