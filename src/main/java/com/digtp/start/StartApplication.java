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
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@Push
@Theme("start")
@PWA(name = "Start", shortName = "Start", offline = false)
@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("java:S1948") // Framework pattern: Spring Boot app contains non-serializable deps
// Suppressed globally in sonar-project.properties (e7),
// but required for Gradle SonarLint plugin
public class StartApplication implements AppShellConfigurator {

    private final Environment environment;

    public static void main(final String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }

    @Bean
    @Primary
    @ConfigurationProperties("main.datasource")
    DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("main.datasource.hikari")
    DataSource dataSource(final DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @EventListener
    public void printApplicationUrl(final ApplicationStartedEvent event) {
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
