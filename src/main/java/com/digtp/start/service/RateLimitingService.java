/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.service;

import com.digtp.start.config.RateLimitingConfig;
import com.github.benmanes.caffeine.cache.Cache;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

/**
 * Service for rate limiting operations.
 *
 * <p>Provides rate limiting functionality to protect endpoints from abuse,
 * particularly login endpoint from brute-force attacks.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitingService {

    private final Cache<String, Bucket> loginRateLimitCache;

    /**
     * Checks if login attempt is allowed for the given IP address.
     *
     * <p>Uses token bucket algorithm to limit login attempts per IP.
     * Returns true if attempt is allowed, false if rate limit exceeded.
     *
     * @param ipAddress IP address of the client, must not be null
     * @return true if login attempt is allowed, false if rate limit exceeded
     */
    public boolean isLoginAllowed(@NonNull final String ipAddress) {
        final Bucket bucket =
                loginRateLimitCache.get(ipAddress, _key -> RateLimitingConfig.createLoginRateLimitBucket());
        final boolean allowed = bucket.tryConsume(1);
        if (!allowed) {
            log.warn("Login rate limit exceeded for IP: {}", ipAddress);
        }
        return allowed;
    }

    /**
     * Gets remaining login attempts for the given IP address.
     *
     * <p>Returns the number of tokens available in the bucket,
     * which represents remaining login attempts.
     *
     * @param ipAddress IP address of the client, must not be null
     * @return number of remaining login attempts
     */
    public long getRemainingLoginAttempts(@NonNull final String ipAddress) {
        final Bucket bucket = loginRateLimitCache.getIfPresent(ipAddress);
        if (bucket == null) {
            return RateLimitingConfig.MAX_LOGIN_ATTEMPTS;
        }
        return bucket.getAvailableTokens();
    }
}
