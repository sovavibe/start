/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.security;

import io.jmix.core.JmixSecurityFilterChainOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * This configuration complements standard security configurations that come from Jmix modules
 * (security-flowui, oidc, authserver).
 * <p>
 * You can configure custom API endpoints security by defining {@link SecurityFilterChain} beans
 * in this class. In most cases, custom SecurityFilterChain must be applied first, so the proper
 * {@link org.springframework.core.annotation.Order} should be defined for the bean. The order
 * value from the {@link io.jmix.core.JmixSecurityFilterChainOrder#CUSTOM} is guaranteed to be
 * smaller than any other filter chain order from Jmix.
 * <p>
 * Example:
 *
 * <pre>
 * &#064;Bean
 * &#064;Order(JmixSecurityFilterChainOrder.CUSTOM)
 * SecurityFilterChain publicFilterChain(HttpSecurity http) throws Exception {
 *     http.securityMatcher("/public/**")
 *             .authorizeHttpRequests(authorize ->
 *                     authorize.anyRequest().permitAll()
 *             );
 *     return http.build();
 * }
 * </pre>
 *
 * @see io.jmix.securityflowui.security.FlowuiVaadinWebSecurity
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
// : Copyright header is standard and required
// PMD.AtLeastOneConstructor: Lombok @RequiredArgsConstructor generates constructor (PMD recognizes this)
// PMD.CommentRequired: All methods have JavaDoc comments
// PMD.GuardLogStatement: SLF4J handles log level checks internally (PMD recognizes this)
// PMD.SignatureDeclareThrowsException: Spring Security HttpSecurity API requires throws Exception (PMD recognizes this)
// PMD.CommentDefaultAccessModifier: All methods are public @Bean methods (PMD recognizes this)
public class StartSecurityConfiguration {

    /**
     * Public API endpoints filter chain.
     * Applied before Jmix default security chains.
     *
     * @param http HttpSecurity instance to configure
     * @return configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    @Order(JmixSecurityFilterChainOrder.CUSTOM)
    SecurityFilterChain publicFilterChain(final HttpSecurity http) throws Exception {
        http.securityMatcher("/public/**")
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());

        log.info("Public security filter chain configured for /public/** endpoints");
        return http.build();
    }

    /**
     * Vaadin Push endpoints filter chain.
     * Fixes Spring Security warning about using web.ignoring() instead of authorizeHttpRequests().
     * Applied before Jmix default security chains.
     *
     * @param http HttpSecurity instance to configure
     * @return configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    @Order(JmixSecurityFilterChainOrder.CUSTOM - 1)
    SecurityFilterChain vaadinPushFilterChain(final HttpSecurity http) throws Exception {
        http.securityMatcher("/VAADIN/push/**")
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());

        log.info("Vaadin Push security filter chain configured for /VAADIN/push/** endpoints");
        return http.build();
    }
}
