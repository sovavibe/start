/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Service for structured audit logging of business events.
 *
 * <p>Provides methods for logging business events to the audit log (audit.log file).
 * Uses dedicated logger 'com.digtp.start.audit' configured in logback-spring.xml.
 *
 * <p>All audit events are logged with structured format including:
 * <ul>
 *   <li>Event type (USER_CREATED, USER_UPDATED, etc.)</li>
 *   <li>Entity ID and relevant identifiers</li>
 *   <li>Current user context (from SecurityContext)</li>
 *   <li>Timestamp (automatically added by logback)</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
// Framework patterns suppressed via @SuppressWarnings (Palantir Baseline defaults):
// PMD.AtLeastOneConstructor: Lombok @RequiredArgsConstructor generates constructor (PMD recognizes this)
// PMD.CommentRequired: All methods have JavaDoc comments
// PMD.GuardLogStatement: SLF4J handles log level checks internally (PMD recognizes this)
public class AuditService {

    /**
     * Dedicated audit logger configured in logback-spring.xml.
     *
     * <p>Logs to audit.log file with 365 days retention for compliance and audit purposes.
     */
    private static final Logger auditLogger = LoggerFactory.getLogger("com.digtp.start.audit");

    /**
     * Logs user creation event.
     *
     * @param userId   created user ID, must not be null
     * @param username created user username, must not be null
     */
    public void logUserCreated(@Nullable final UUID userId, @Nullable final String username) {
        final String currentUser = getCurrentUsername();
        auditLogger.info("USER_CREATED: userId={}, username={}, createdBy={}", userId, username, currentUser);
    }

    /**
     * Logs user update event.
     *
     * @param userId   updated user ID, must not be null
     * @param username updated user username, must not be null
     */
    public void logUserUpdated(@Nullable final UUID userId, @Nullable final String username) {
        final String currentUser = getCurrentUsername();
        auditLogger.info("USER_UPDATED: userId={}, username={}, updatedBy={}", userId, username, currentUser);
    }

    /**
     * Logs user deletion event.
     *
     * @param userId   deleted user ID, must not be null
     * @param username deleted user username, must not be null
     */
    public void logUserDeleted(@Nullable final UUID userId, @Nullable final String username) {
        final String currentUser = getCurrentUsername();
        auditLogger.info("USER_DELETED: userId={}, username={}, deletedBy={}", userId, username, currentUser);
    }

    /**
     * Logs successful login event.
     *
     * @param username username of the user who logged in, must not be null
     */
    public void logLogin(@Nullable final String username) {
        auditLogger.info("LOGIN_SUCCESS: username={}", username);
    }

    /**
     * Logs failed login attempt.
     *
     * @param username username of the user who attempted to log in, must not be null
     * @param reason   reason for login failure (e.g., "Bad credentials", "Account locked")
     */
    public void logLoginFailed(@Nullable final String username, @Nullable final String reason) {
        auditLogger.warn("LOGIN_FAILED: username={}, reason={}", username, reason);
    }

    /**
     * Logs password change event.
     *
     * @param userId   user ID whose password was changed, must not be null
     * @param username username of the user whose password was changed, must not be null
     */
    public void logPasswordChanged(@Nullable final UUID userId, @Nullable final String username) {
        final String currentUser = getCurrentUsername();
        auditLogger.info("PASSWORD_CHANGED: userId={}, username={}, changedBy={}", userId, username, currentUser);
    }

    /**
     * Extracts current username from SecurityContext.
     *
     * <p>Returns username of the currently authenticated user, or "system" if not authenticated
     * (e.g., for system operations).
     *
     * @return current username or "system" if not authenticated
     */
    @Nullable
    private String getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "system";
        }

        final Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }

        // Fallback: use authentication name
        return authentication.getName();
    }
}
