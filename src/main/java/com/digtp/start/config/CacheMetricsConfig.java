/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

/**
 * Configuration for cache metrics with Micrometer.
 *
 * <p>Registers cache metrics for Jmix internal caches (row-level-roles-cache,
 * resource-roles-cache, jmix-eclipselink-query-cache) to enable monitoring
 * via Micrometer.
 *
 * <p>Note: Jmix creates caches internally using Caffeine. This configuration
 * attempts to register metrics for these caches. If caches are not available
 * or already registered, warnings may appear but are non-critical.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
// Framework patterns suppressed via @SuppressWarnings (Palantir Baseline defaults):
// - PMD.AtLeastOneConstructor, PMD.CommentRequired, PMD.GuardLogStatement
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.CommentRequired", "PMD.GuardLogStatement"})
public class CacheMetricsConfig implements ApplicationListener<ApplicationReadyEvent> {

    private final MeterRegistry meterRegistry;

    @Nullable
    private final CacheManager cacheManager;

    /**
     * Registers cache metrics after application is ready.
     *
     * <p>Waits for application to be fully initialized before registering metrics,
     * ensuring all caches are available.
     *
     * @param _event application ready event (unused, required by interface)
     */
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent _event) {
        if (cacheManager != null) {
            registerCacheMetrics(cacheManager);
        } else {
            log.debug("CacheManager not available, skipping cache metrics registration");
        }
    }

    /**
     * Registers metrics for all caches in the CacheManager.
     *
     * <p>Iterates through all cache names and attempts to register metrics
     * for Caffeine caches. Logs successful registrations and skips caches
     * that don't support metrics.
     *
     * @param manager Spring CacheManager (renamed to avoid hiding field)
     */
    private void registerCacheMetrics(final CacheManager manager) {
        final Collection<String> cacheNames = manager.getCacheNames();
        int registeredCount = 0;

        for (final String cacheName : cacheNames) {
            try {
                final Cache cache = manager.getCache(cacheName);
                if (cache != null
                        && cache.getNativeCache()
                                // CHECKSTYLE:OFF: AvoidFullyQualifiedNames - FQN required to resolve name conflict:
                                // com.github.benmanes.caffeine.cache.Cache vs org.springframework.cache.Cache
                                instanceof com.github.benmanes.caffeine.cache.Cache<?, ?> caffeineCache) {
                    // CHECKSTYLE:ON: AvoidFullyQualifiedNames
                    // Register metrics for Caffeine cache
                    CaffeineCacheMetrics.monitor(meterRegistry, caffeineCache, cacheName);
                    registeredCount++;
                    log.debug("Registered metrics for cache: {}", cacheName);
                }
            } catch (final Exception exception) {
                // Cache may not support metrics or may not be a Caffeine cache
                log.debug("Could not register metrics for cache {}: {}", cacheName, exception.getMessage());
            }
        }

        if (registeredCount > 0) {
            log.info("Registered metrics for {} cache(s)", registeredCount);
        } else {
            log.debug("No cache metrics registered (caches may not be Caffeine-based or not yet initialized)");
        }
    }
}
