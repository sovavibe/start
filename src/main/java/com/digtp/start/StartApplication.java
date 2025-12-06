/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import java.util.Objects;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

/**
 * Main Spring Boot application class.
 *
 * <p>Configures Vaadin Push, theme, and PWA settings. Provides data source
 * configuration and application startup event handling.
 */
@Push
@Theme("start")
@PWA(name = "Start", shortName = "Start", offline = false)
@SpringBootApplication(exclude = ProjectInfoAutoConfiguration.class)
@Slf4j
@RequiredArgsConstructor
// Framework: Spring Boot application contains non-serializable framework dependencies (Environment, etc.)
@SuppressWarnings({
    // Framework: Spring Boot application contains non-serializable framework dependencies (Environment, etc.)
    "java:S1948", // non-serializable field
    // Framework: Error Prone StrictUnusedVariable requires underscore prefix for unused variables
    "java:S117", // unused variable
    // Framework: AppShellConfigurator interface requires methods with long parameter names
    "PMD.LongVariable", // long variable name
    // Framework: Vaadin route parameters use framework conventions
    "PMD.FormalParameterNamingConventions", // parameter naming
    // Framework: AppShellConfigurator is not Serializable. No serialVersionUID needed
    "PMD.MissingSerialVersionUID" // missing serial version UID
})
public class StartApplication implements AppShellConfigurator {

    /**
     * Spring environment for accessing application properties.
     */
    private final transient Environment environment;

    /**
     * Application entry point.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }

    /**
     * Creates primary data source properties bean.
     *
     * @return data source properties
     */
    @Bean
    @Primary
    @ConfigurationProperties("main.datasource")
    DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Creates primary data source bean.
     *
     * @param dataSourceProperties data source properties
     * @return data source
     */
    @Bean
    @Primary
    @ConfigurationProperties("main.datasource.hikari")
    DataSource dataSource(final DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    /**
     * Logs application URL and active profiles on startup.
     *
     * @param _event application started event
     */
    @EventListener
    public void printApplicationUrl(final ApplicationStartedEvent _event) {
        final String port = environment.getProperty("local.server.port", "8080");
        final String contextPathProperty = environment.getProperty("server.servlet.context-path");
        final String contextPath = Objects.requireNonNullElse(contextPathProperty, "");
        final String activeProfiles = String.join(
                ", ",
                environment.getActiveProfiles().length > 0
                        ? environment.getActiveProfiles()
                        : new String[] {"default"});
        log.info("Application started successfully");
        log.info("URL: http://localhost:{}{}", port, contextPath);
        log.info("Active profiles: {}", activeProfiles);
        log.debug("Application context initialized, ready to serve requests");
    }
}
