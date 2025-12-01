package com.digtp.start.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.digtp.start.entity.User;
import com.digtp.start.test_support.AuthenticatedAsAdmin;
import io.jmix.core.security.SystemAuthenticator;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith({SpringExtension.class, AuthenticatedAsAdmin.class})
class DatabaseUserRepositoryTest {

    @Autowired
    private DatabaseUserRepository userRepository;

    @Autowired
    private SystemAuthenticator systemAuthenticator;

    @Test
    void testGetUserClass() {
        final Class<?> userClass = userRepository.getUserClass();

        assertThat(userClass).isEqualTo(User.class);
    }

    @Test
    void testGetSystemUser() {
        systemAuthenticator.begin();

        try {
            final User systemUser = userRepository.getSystemUser();

            assertThat(systemUser).isNotNull();
            assertThat(systemUser.getUsername()).isEqualTo("system");

            final Collection<? extends GrantedAuthority> authorities = systemUser.getAuthorities();
            assertThat(authorities).isNotEmpty();
            assertThat(authorities.stream()
                            .map(GrantedAuthority::getAuthority)
                            .anyMatch(auth -> auth.contains(FullAccessRole.CODE)))
                    .isTrue();
        } finally {
            systemAuthenticator.end();
        }
    }

    @Test
    void testGetAnonymousUser() {
        final User anonymousUser = userRepository.getAnonymousUser();

        assertThat(anonymousUser).isNotNull();
        assertThat(anonymousUser.getUsername()).isEqualTo("anonymous");
    }
}
