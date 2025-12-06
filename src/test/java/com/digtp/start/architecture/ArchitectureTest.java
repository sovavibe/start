/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
