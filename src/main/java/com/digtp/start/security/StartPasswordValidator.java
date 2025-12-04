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

import com.digtp.start.config.SecurityConstants;
import com.digtp.start.entity.User;
import io.jmix.securityflowui.password.PasswordValidationContext;
import io.jmix.securityflowui.password.PasswordValidationException;
import io.jmix.securityflowui.password.PasswordValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Custom password validator for User entities.
 * Enforces minimum password length and other security requirements.
 *
 * <p>This class is not designed for extension. All methods are final
 * and should not be overridden.
 */
@Component("startPasswordValidator")
@Slf4j
// Framework patterns suppressed via @SuppressWarnings (Palantir Baseline defaults):
// - PMD.CommentSize, PMD.CommentRequired, PMD.GuardLogStatement
// - PMD.AtLeastOneConstructor, PMD.LawOfDemeter
@SuppressWarnings("PMD.MissingSerialVersionUID")
public final class StartPasswordValidator implements PasswordValidator<User> {

    @Override
    public void validate(final PasswordValidationContext<User> context) throws PasswordValidationException {
        final String password = context.getPassword();

        if (SecurityConstants.isNullOrEmpty(password)) {
            throw new PasswordValidationException("Password cannot be empty");
        }

        if (password.length() < SecurityConstants.MIN_PASSWORD_LENGTH) {
            throw new PasswordValidationException(
                    "Password must be at least %d characters long".formatted(SecurityConstants.MIN_PASSWORD_LENGTH));
        }

        final User user = context.getUser();
        final String username = user != null ? user.getUsername() : "new";
        log.debug("Password validation passed: username={}", username);
    }
}
