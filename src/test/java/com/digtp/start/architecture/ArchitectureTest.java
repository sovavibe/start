/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.flowui.view.ViewController;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Architecture tests to enforce clean architecture and prevent layer violations.
 *
 * <p>These tests ensure:
 * <ul>
 *   <li>Services cannot access Views (presentation layer)</li>
 *   <li>Views cannot access Repositories directly (data layer)</li>
 *   <li>Entities are properly located in entity package</li>
 *   <li>No cyclic dependencies between packages</li>
 * </ul>
 */
@AnalyzeClasses(packages = "com.digtp.start", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    /**
     * Rule: Services should not access Views (presentation layer).
     */
    @ArchTest
    static final ArchRule servicesShouldNotAccessViews = noClasses()
            .that()
            .resideInAPackage("..service..")
            .should()
            .accessClassesThat()
            .resideInAPackage("..view..")
            .because("Services should not depend on presentation layer (Views)");

    /**
     * Rule: Views should not access Repositories directly.
     */
    @ArchTest
    static final ArchRule viewsShouldNotAccessRepositoriesDirectly = noClasses()
            .that()
            .resideInAPackage("..view..")
            .should()
            .accessClassesThat()
            .resideInAPackage("..repository..")
            .because("Views should access data through services, not repositories directly");

    /**
     * Rule: Entities must be located in entity package.
     */
    @ArchTest
    static final ArchRule entitiesShouldBeInEntityPackage = classes()
            .that()
            .areAnnotatedWith(JmixEntity.class)
            .or()
            .areAnnotatedWith(Entity.class)
            .should()
            .resideInAPackage("..entity..")
            .because("Entities must be located in entity package for consistency");

    /**
     * Rule: No cyclic dependencies between packages.
     */
    @ArchTest
    static final ArchRule noCyclesBetweenPackages = SlicesRuleDefinition.slices()
            .matching("com.digtp.start.(*)..")
            .should()
            .beFreeOfCycles()
            .because("Cyclic dependencies between packages indicate architectural problems");

    /**
     * Rule: Layered architecture must be respected.
     *
     * <p>Clean Architecture layers: View -> Service -> Entity.
     * Config and Security layers may access Service and Entity.
     */
    @ArchTest
    static final ArchRule layeredArchitectureIsRespected = layeredArchitecture()
            .consideringAllDependencies()
            .layer("View")
            .definedBy("..view..")
            .layer("Service")
            .definedBy("..service..")
            .layer("Entity")
            .definedBy("..entity..")
            .layer("Config")
            .definedBy("..config..")
            .layer("Security")
            .definedBy("..security..")
            .whereLayer("View")
            .mayNotBeAccessedByAnyLayer()
            .whereLayer("Service")
            .mayOnlyBeAccessedByLayers("View", "Config", "Security")
            .whereLayer("Entity")
            .mayOnlyBeAccessedByLayers("Service", "View", "Security", "Config")
            .because("Clean Architecture: Views -> Services -> Entities");

    /**
     * Rule: Services must be annotated with @Service.
     */
    @ArchTest
    static final ArchRule servicesMustBeAnnotated = classes()
            .that()
            .resideInAPackage("..service..")
            .and()
            .haveSimpleNameEndingWith("Service")
            .should()
            .beAnnotatedWith(Service.class)
            .because("All service classes must be annotated with @Service for Spring DI");

    /**
     * Rule: Views must follow naming convention (end with "View").
     */
    @ArchTest
    static final ArchRule viewsNamingConvention = classes()
            .that()
            .resideInAPackage("..view..")
            .and()
            .areAnnotatedWith(ViewController.class)
            .should()
            .haveSimpleNameEndingWith("View")
            .because("Jmix views must end with 'View' suffix for consistency");

    /**
     * Rule: Entities should not have DTO suffix.
     *
     * <p>Entities annotated with @JmixEntity should be domain models, not DTOs.
     */
    @ArchTest
    static final ArchRule entitiesNamingConvention = classes()
            .that()
            .areAnnotatedWith(JmixEntity.class)
            .should()
            .haveSimpleNameNotEndingWith("DTO")
            .andShould()
            .haveSimpleNameNotEndingWith("Dto")
            .because("Entities are domain models, not DTOs");

    /**
     * Rule: Services should use @Transactional for data operations.
     *
     * <p>Services annotated with @Service should have @Transactional at class or method level.
     */
    @ArchTest
    static final ArchRule servicesShouldUseTransactional = classes()
            .that()
            .resideInAPackage("..service..")
            .and()
            .areAnnotatedWith(Service.class)
            .should()
            .beAnnotatedWith(Transactional.class)
            .because("Services should use @Transactional for proper transaction management");

    /**
     * Rule: No field injection in services (use constructor injection).
     *
     * <p>Services should use constructor injection via @RequiredArgsConstructor,
     * not @Autowired field injection.
     */
    @ArchTest
    static final ArchRule noFieldInjectionInServices = noFields()
            .that()
            .areDeclaredInClassesThat()
            .resideInAPackage("..service..")
            .should()
            .beAnnotatedWith(Autowired.class)
            .because("Services should use constructor injection, not @Autowired field injection");

    /**
     * Verifies that architecture rules are properly loaded and executed.
     */
    @Test
    void architectureRulesAreDefined() {
        // This test ensures ArchUnit rules are loaded and executed
        // Individual rules are tested via @ArchTest annotations above
        final JavaClasses classes = new ClassFileImporter().importPackages("com.digtp.start");
        Assertions.assertFalse(classes.isEmpty(), "No classes found for architecture testing");
    }
}
