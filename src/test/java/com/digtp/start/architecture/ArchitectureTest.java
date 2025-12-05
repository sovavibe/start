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
// ArchUnit test: rule names are self-documenting (e.g., "servicesShouldNotDependOnViews")
// ArchUnit convention - descriptive rule names don't require additional comments
// Rules use package-private access (ArchUnit convention)
// Example: static final ArchRule servicesShouldNotDependOnViews = ... - package-private is standard for ArchUnit
// ArchUnit convention uses snake_case for rule names (e.g., "services_should_not_depend_on_views")
// ArchUnit standard - snake_case makes rules more readable in test output
// Test class doesn't need explicit constructor
// ArchUnit rule names are descriptive
// ArchUnit requires ArchRule type (not interface)
@SuppressWarnings({
    // Test: ArchUnit rule names are descriptive
    "PMD.CommentRequired",
    // Test: ArchUnit requires ArchRule type (not interface)
    "PMD.CommentDefaultAccessModifier",
    // Test: ArchUnit rule names are descriptive
    "PMD.FieldNamingConventions",
    // Test: Test class doesn't need explicit constructor
    "PMD.AtLeastOneConstructor",
    // Test: ArchUnit rule names are descriptive
    "PMD.LongVariable",
    // Test: ArchUnit requires ArchRule type (not interface)
    "PMD.LooseCoupling",
    // Test: Some tests are clearer as separate methods rather than parameterized
    "java:S5976",
    // Test: Multiple assertions on same object are acceptable in tests for clarity
    "java:S5853",
    // Test: Test methods may have similar structure but test different scenarios
    "java:S4144"
})
class ArchitectureTest {

    @ArchTest
    // Checkstyle:ConstantName excluded via .baseline/checkstyle/custom-suppressions.xml
    // PMD.FieldNamingConventions suppressed at class level (ArchUnit snake_case convention)
    static final ArchRule services_should_not_access_views = noClasses()
            .that()
            .resideInAPackage("..service..")
            .should()
            .accessClassesThat()
            .resideInAPackage("..view..")
            .because("Services should not depend on presentation layer (Views)");

    @ArchTest
    // Checkstyle:ConstantName excluded via .baseline/checkstyle/custom-suppressions.xml
    // PMD.FieldNamingConventions suppressed at class level (ArchUnit snake_case convention)
    static final ArchRule views_should_not_access_repositories_directly = noClasses()
            .that()
            .resideInAPackage("..view..")
            .should()
            .accessClassesThat()
            .resideInAPackage("..repository..")
            .because("Views should access data through services, not repositories directly");

    @ArchTest
    // Checkstyle:ConstantName excluded via .baseline/checkstyle/custom-suppressions.xml
    // PMD.FieldNamingConventions suppressed at class level (ArchUnit snake_case convention)
    static final ArchRule entities_should_be_in_entity_package = classes()
            .that()
            .areAnnotatedWith(JmixEntity.class)
            .or()
            .areAnnotatedWith(Entity.class)
            .should()
            .resideInAPackage("..entity..")
            .because("Entities must be located in entity package for consistency");

    @ArchTest
    // Checkstyle:ConstantName excluded via .baseline/checkstyle/custom-suppressions.xml
    // PMD.FieldNamingConventions, PMD.LooseCoupling suppressed at class level (ArchUnit conventions)
    static final ArchRule no_cycles_between_packages = SlicesRuleDefinition.slices()
            .matching("com.digtp.start.(*)..")
            .should()
            .beFreeOfCycles()
            .because("Cyclic dependencies between packages indicate architectural problems");

    @Test
    // PMD.LooseCoupling suppressed at class level (ArchUnit ArchRule type required)
    void architectureRulesAreDefined() {
        // This test ensures ArchUnit rules are loaded and executed
        // Individual rules are tested via @ArchTest annotations above
        final JavaClasses classes = new ClassFileImporter().importPackages("com.digtp.start");
        // PMD: UseCollectionIsEmpty - isEmpty() not available on JavaClasses, size() check is appropriate
        Assertions.assertFalse(classes.isEmpty(), "No classes found for architecture testing");
    }
}
