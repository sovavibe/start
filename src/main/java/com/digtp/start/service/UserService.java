package com.digtp.start.service;

import com.digtp.start.entity.User;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final PasswordEncoder passwordEncoder;

    /**
     * Encodes a plain text password using the configured password encoder.
     *
     * @param password plain text password to encode
     * @return encoded password
     */
    @NonNull
    public String encodePassword(@NonNull final String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Validates that password and confirmation password match.
     *
     * @param password password
     * @param confirmPassword confirmation password
     * @return true if passwords match, false otherwise
     */
    public boolean validatePasswordConfirmation(@NonNull final String password, @NonNull final String confirmPassword) {
        return Objects.equals(password, confirmPassword);
    }

    /**
     * Prepares user for save by encoding password if provided.
     * Logs the operation for audit purposes.
     *
     * @param user user entity to prepare
     * @param password plain text password (can be null for existing users)
     * @param isNew true if creating new user, false if updating
     */
    public void prepareUserForSave(@NonNull final User user, final String password, final boolean isNew) {
        if (isNew && password != null) {
            final String encodedPassword = encodePassword(password);
            user.setPassword(encodedPassword);
            log.info("Prepared new user for save: username={}", user.getUsername());
        } else if (!isNew) {
            log.info("Prepared user for update: id={}, username={}", user.getId(), user.getUsername());
        }
    }
}
