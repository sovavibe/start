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
public final class StartPasswordValidator implements PasswordValidator<User> {

    @Override
    public void validate(final PasswordValidationContext<User> context) throws PasswordValidationException {
        final String password = context.getPassword();

        if (password == null || password.isEmpty()) {
            throw new PasswordValidationException("Password cannot be empty");
        }

        if (password.length() < SecurityConstants.MIN_PASSWORD_LENGTH) {
            throw new PasswordValidationException(String.format(
                    "Password must be at least %d characters long", SecurityConstants.MIN_PASSWORD_LENGTH));
        }

        final User user = context.getUser();
        log.debug("Password validation passed for user: username={}", user != null ? user.getUsername() : "new");
    }
}
