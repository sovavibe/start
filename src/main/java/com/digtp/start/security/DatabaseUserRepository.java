package com.digtp.start.security;

import com.digtp.start.entity.User;
import io.jmix.securitydata.user.AbstractDatabaseUserRepository;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * Database-backed user repository implementation.
 *
 * <p>Extends Jmix AbstractDatabaseUserRepository to provide user authentication
 * and authorization data from the database. Initializes system and anonymous users
 * with appropriate roles.
 */
@Primary
@Component("userRepository")
@RequiredArgsConstructor
@Slf4j
public class DatabaseUserRepository extends AbstractDatabaseUserRepository<User> {

    @Override
    protected Class<User> getUserClass() {
        return User.class;
    }

    @Override
    protected void initSystemUser(final User systemUser) {
        final Collection<GrantedAuthority> authorities = getGrantedAuthoritiesBuilder()
                .addResourceRole(FullAccessRole.CODE)
                .build();
        systemUser.setAuthorities(authorities);
        log.info("System user initialized with full access role: username={}", systemUser.getUsername());
    }

    @Override
    protected void initAnonymousUser(final User anonymousUser) {
        log.debug("Anonymous user initialized: username={}", anonymousUser.getUsername());
    }
}
