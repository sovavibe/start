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
// Test patterns: ArchUnit test conventions require snake_case rule names and ArchRule types
// - PMD rules handled by Baseline: CommentSize, CommentRequired, CommentDefaultAccessModifier,
//   AtLeastOneConstructor, LongVariable
// - ArchUnit-specific: FieldNamingConventions (snake_case), LooseCoupling (ArchRule type required)
// - Checkstyle rules excluded via .baseline/checkstyle/custom-suppressions.xml: ConstantName (ArchUnit convention)
@SuppressWarnings({
    // Copyright header is standard and required (Apache License)
    "PMD.CommentRequired", // Test class documentation
    "PMD.CommentDefaultAccessModifier", // ArchUnit test rules use package-private
    "PMD.FieldNamingConventions", // ArchUnit convention uses snake_case for rule names
    "PMD.AtLeastOneConstructor", // Test class doesn't need explicit constructor
    "PMD.LongVariable", // ArchUnit rule names are descriptive
    "PMD.LooseCoupling" // ArchUnit requires ArchRule type (not interface)
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
