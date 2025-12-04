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
package com.digtp.start.service;

import com.digtp.start.config.SecurityConstants;
import com.digtp.start.entity.User;
import com.palantir.logsafe.Preconditions;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for user management operations.
 *
 * <p>Handles user creation, password validation, and encoding.
 * Provides business logic for user entity operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
// Framework patterns suppressed via @SuppressWarnings (Palantir Baseline defaults):
// - PMD.CommentSize, PMD.AtLeastOneConstructor, PMD.CommentRequired
// - PMD.GuardLogStatement, PMD.LawOfDemeter
public class UserService {

    private final PasswordEncoder passwordEncoder;

    /**
     * Encodes a plain text password using the configured password encoder.
     *
     * <p>Uses the Spring Security PasswordEncoder bean configured in the application.
     * The encoded password format is determined by the encoder implementation
     * (typically BCrypt: {@code {bcrypt}$2a$10...}).
     *
     * @param password plain text password to encode, must not be null
     * @return encoded password hash, never null
     * @throws IllegalArgumentException if password is null or empty
     * @since 1.0
     * <p>Example:
     * <pre>{@code
     * String encoded = userService.encodePassword("myPassword123");
     * // Result: "{bcrypt}$2a$10$N9qo8uLOickgx2ZMRZoMye..."
     * }</pre>
     */
    @NonNull
    public String encodePassword(@NonNull final String password) {
        log.debug("Encoding password for user");
        return passwordEncoder.encode(password);
    }

    /**
     * Validates that password and confirmation password match.
     *
     * <p>Compares the password and confirmation password for equality.
     * Used during user creation and password change operations to ensure
     * the user entered the intended password correctly.
     *
     * @param password password value, must not be null
     * @param confirmPassword confirmation password value, must not be null
     * @return true if passwords match exactly, false otherwise
     * @since 1.0
     * <p>Example:
     * <pre>{@code
     * if (!userService.validatePasswordConfirmation(password, confirmPassword)) {
     *     // Show error: passwords do not match
     * }
     * }</pre>
     */
    public boolean validatePasswordConfirmation(@NonNull final String password, @NonNull final String confirmPassword) {
        final boolean matches = Objects.equals(password, confirmPassword);
        if (!matches) {
            log.warn("Password confirmation validation failed");
        }
        return matches;
    }

    /**
     * Validates password strength requirements.
     *
     * <p>Enforces minimum password length policy as defined in
     * {@link SecurityConstants#MIN_PASSWORD_LENGTH}. Currently requires
     * passwords to be at least 8 characters long.
     *
     * @param password password to validate, must not be null
     * @throws IllegalArgumentException if password length is less than minimum required length
     * @see SecurityConstants#MIN_PASSWORD_LENGTH
     * <p>Example:
     * <pre>{@code
     * try {
     *     userService.validatePasswordStrength("short");
     * } catch (IllegalArgumentException e) {
     *     // Password too short
     * }
     * }</pre>
     * @since 1.0
     */
    public void validatePasswordStrength(@NonNull final String password) {
        if (password.length() < SecurityConstants.MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Password must be at least %d characters long".formatted(SecurityConstants.MIN_PASSWORD_LENGTH));
        }
    }

    /**
     * Prepares user for save by encoding password if provided.
     *
     * <p>Handles password encoding for both new user creation and password updates.
     * For new users, password is required and will be validated and encoded.
     * For existing users, password is optional - if provided, it will be validated
     * and encoded, otherwise the existing password is preserved.
     *
     * <p>This method performs the following operations:
     * <ul>
     *   <li>Validates password strength (minimum length)</li>
     *   <li>Encodes password using configured PasswordEncoder</li>
     *   <li>Sets encoded password on user entity</li>
     *   <li>Logs operation for audit purposes</li>
     * </ul>
     *
     * @param user user entity to prepare, must not be null
     * @param password plain text password (can be null for existing users when not changing password)
     * @param isNew true if creating new user, false if updating existing user
     * @throws IllegalArgumentException if password validation fails, or if password is required
     * (new user) but missing
     * @since 1.0
     * <p>Example:
     * <pre>{@code
     * // Create new user
     * User newUser = dataManager.create(User.class);
     * newUser.setUsername("john.doe");
     * userService.prepareUserForSave(newUser, "securePassword123", true);
     * dataManager.save(newUser);
     *
     * // Update existing user (change password)
     * userService.prepareUserForSave(existingUser, "newPassword456", false);
     *
     * // Update existing user (no password change)
     * userService.prepareUserForSave(existingUser, null, false);
     * }</pre>
     */
    @SuppressWarnings(
            "NullAway") // password is validated before use - isNotNullOrEmpty and requirePasswordForNewUser ensure
    // non-null
    public void prepareUserForSave(@NonNull final User user, @Nullable final String password, final boolean isNew) {
        if (isNew) {
            // requirePasswordForNewUser validates and returns non-null password
            final String nonNullPassword = requirePasswordForNewUser(password);
            encodeAndSetPassword(user, nonNullPassword);
            log.info("Prepared new user for save: username={}", user.getUsername());
        } else {
            // Check if password is provided for update
            if (SecurityConstants.isNotNullOrEmpty(password)) {
                // After isNotNullOrEmpty check, password is guaranteed to be non-null
                // Preconditions.checkNotNull ensures non-null and satisfies NullAway
                // NullAway: password is guaranteed non-null after isNotNullOrEmpty check
                final String nonNullPassword = Preconditions.checkNotNull(password, "Password must not be null");
                encodeAndSetPassword(user, nonNullPassword);
                log.info("Prepared user password update: id={}, username={}", user.getId(), user.getUsername());
            } else {
                // No password change - log
                log.debug(
                        "Prepared user for update (no password change): id={}, username={}",
                        user.getId(),
                        user.getUsername());
            }
        }
    }

    /**
     * Validates and encodes password, then sets it on the user entity.
     *
     * <p>Performs password strength validation and encoding in a single operation.
     * This method encapsulates the common pattern of validating and encoding passwords.
     *
     * @param user user entity to set password on, must not be null
     * @param password plain text password to validate and encode, must not be null or empty
     */
    private void encodeAndSetPassword(@NonNull final User user, @NonNull final String password) {
        validatePasswordStrength(password);
        final String encodedPassword = encodePassword(password);
        user.setPassword(encodedPassword);
    }

    /**
     * Validates that password is provided for new user creation.
     *
     * <p>Throws IllegalArgumentException if password is null or empty, as new users
     * require a password to be set.
     *
     * @param password password to validate, can be null or empty
     * @return non-null password after validation
     * @throws IllegalArgumentException if password is null or empty
     */
    @NonNull
    // password is validated before use - isNullOrEmpty check ensures non-null after validation
    // Using IllegalArgumentException for simplicity, SafeIllegalArgumentException requires additional dependency
    @SuppressWarnings({"NullAway", "PreferSafeLoggableExceptions"})
    private String requirePasswordForNewUser(@Nullable final String password) {
        if (SecurityConstants.isNullOrEmpty(password)) {
            throw new IllegalArgumentException("Password is required for new users");
        }
        // After isNullOrEmpty check, password is guaranteed to be non-null
        // Preconditions.checkNotNull ensures non-null and satisfies NullAway
        // NullAway: password is guaranteed non-null after isNullOrEmpty check
        return Preconditions.checkNotNull(password, "Password must not be null");
    }
}
