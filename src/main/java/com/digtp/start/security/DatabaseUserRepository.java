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
// Framework patterns suppressed via @SuppressWarnings (Palantir Baseline defaults):
// - PMD.CommentSize, PMD.AtLeastOneConstructor, PMD.CommentRequired, PMD.GuardLogStatement
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
