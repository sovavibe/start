/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for rate limiting using Bucket4j.
 *
 * <p>Provides rate limiting buckets for protecting endpoints from abuse,
 * particularly login endpoint from brute-force attacks.
 */
@Configuration
@Slf4j
public class RateLimitingConfig {

    /**
     * Maximum number of login attempts per IP address per time window.
     */
    public static final int MAX_LOGIN_ATTEMPTS = 5;

    /**
     * Time window for login attempts (minutes).
     */
    private static final int LOGIN_WINDOW_MINUTES = 1;

    /**
     * Cache for storing rate limit buckets per IP address.
     *
     * <p>Uses Caffeine cache with TTL to automatically evict entries
     * after the time window expires, preventing memory leaks.
     *
     * @return cache for rate limit buckets
     */
    @Bean
    public Cache<String, Bucket> loginRateLimitCache() {
        log.info(
                "Configuring login rate limiting: {} attempts per {} minutes",
                MAX_LOGIN_ATTEMPTS,
                LOGIN_WINDOW_MINUTES);
        return Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(LOGIN_WINDOW_MINUTES + 1L, TimeUnit.MINUTES)
                .build();
    }

    /**
     * Creates a new rate limit bucket for login attempts.
     *
     * <p>Allows MAX_LOGIN_ATTEMPTS attempts per LOGIN_WINDOW_MINUTES window.
     * Uses token bucket algorithm with refill strategy.
     *
     * @return configured rate limit bucket
     */
    public static Bucket createLoginRateLimitBucket() {
        final Bandwidth limit = Bandwidth.builder()
                .capacity(MAX_LOGIN_ATTEMPTS)
                .refillIntervally(MAX_LOGIN_ATTEMPTS, Duration.ofMinutes(LOGIN_WINDOW_MINUTES))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }
}
