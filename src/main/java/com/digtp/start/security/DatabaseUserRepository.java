/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
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

    /**
     * Returns User entity class.
     *
     * <p>This method is safe to override. Override to return a custom User subclass.
     *
     * @return User entity class
     */
    @Override
    protected Class<User> getUserClass() {
        return User.class;
    }

    /**
     * Initializes system user with full access role.
     *
     * <p>This method is safe to override. Override to customize system user initialization.
     * Call super.initSystemUser() to preserve default behavior.
     *
     * @param systemUser system user to initialize
     */
    @Override
    protected void initSystemUser(final User systemUser) {
        final Collection<GrantedAuthority> authorities = getGrantedAuthoritiesBuilder()
                .addResourceRole(FullAccessRole.CODE)
                .build();
        systemUser.setAuthorities(authorities);
        log.info("System user initialized with full access role: username={}", systemUser.getUsername());
    }

    /**
     * Initializes anonymous user.
     *
     * <p>This method is safe to override. Override to customize anonymous user initialization.
     * Call super.initAnonymousUser() to preserve default behavior.
     *
     * @param anonymousUser anonymous user to initialize
     */
    @Override
    protected void initAnonymousUser(final User anonymousUser) {
        log.debug("Anonymous user initialized: username={}", anonymousUser.getUsername());
    }
}
