/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.config;

import com.digtp.start.entity.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Servlet Filter for automatic MDC context population.
 *
 * <p>Automatically populates MDC (Mapped Diagnostic Context) with requestId and userId
 * for all HTTP requests. This enables distributed tracing and request correlation
 * across the application logs.
 *
 * <p>Features:
 * <ul>
 *   <li>Generates unique requestId (UUID) for each HTTP request</li>
 *   <li>Extracts userId from SecurityContext if authenticated</li>
 *   <li>Clears MDC after request completion</li>
 *   <li>Supports async requests (Vaadin Push)</li>
 * </ul>
 */
@Component
@Order(1) // Execute early, before security filters
@Slf4j
// Framework patterns: PMD rules handled by Baseline
public final class LoggingMdcFilter implements Filter {

    private static final String REQUEST_ID_KEY = "requestId";
    private static final String USER_ID_KEY = "userId";

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        try {
            final String requestId = generateRequestId();
            MDC.put(REQUEST_ID_KEY, requestId);

            final String userId = extractUserId();
            if (userId != null) {
                MDC.put(USER_ID_KEY, userId);
            }

            // Log request start for debugging (only if DEBUG level is enabled)
            if (log.isDebugEnabled() && request instanceof HttpServletRequest httpRequest) {
                log.debug(
                        "Request started: method={}, path={}, requestId={}",
                        httpRequest.getMethod(),
                        httpRequest.getRequestURI(),
                        requestId);
            }

            chain.doFilter(request, response);
        } finally {
            // Clear MDC after request completion
            MDC.clear();
        }
    }

    /**
     * Generates unique request ID for tracing.
     *
     * @return UUID string as request ID
     */
    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Extracts user ID from SecurityContext if authenticated.
     *
     * <p>Supports both Jmix UserDetails (User entity) and standard Spring Security UserDetails.
     * Returns user ID (UUID) for User entity, or username for other UserDetails implementations.
     *
     * @return user ID (UUID) or username, null if not authenticated
     */
    @Nullable
    private String extractUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        final Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            // User entity - return UUID
            return user.getId() != null ? user.getId().toString() : null;
        } else if (principal instanceof UserDetails userDetails) {
            // Standard Spring Security UserDetails - return username
            return userDetails.getUsername();
        }

        // Fallback: use principal name if available
        return authentication.getName();
    }
}
