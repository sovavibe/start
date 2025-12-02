package com.digtp.start.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
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
@SuppressWarnings("PMD.FieldNamingConventions") // ArchUnit convention uses snake_case for rule names
class ArchitectureTest {

    @ArchTest
    @SuppressWarnings({"Checkstyle:ConstantName", "PMD.FieldNamingConventions"})
    // ArchUnit convention uses snake_case for rule names
    static final ArchRule services_should_not_access_views = noClasses()
            .that()
            .resideInAPackage("..service..")
            .should()
            .accessClassesThat()
            .resideInAPackage("..view..")
            .because("Services should not depend on presentation layer (Views)");

    @ArchTest
    @SuppressWarnings({"Checkstyle:ConstantName", "PMD.FieldNamingConventions"})
    // ArchUnit convention uses snake_case for rule names
    static final ArchRule views_should_not_access_repositories_directly = noClasses()
            .that()
            .resideInAPackage("..view..")
            .should()
            .accessClassesThat()
            .resideInAPackage("..repository..")
            .because("Views should access data through services, not repositories directly");

    @ArchTest
    @SuppressWarnings({"Checkstyle:ConstantName", "PMD.FieldNamingConventions"})
    // ArchUnit convention uses snake_case for rule names
    static final ArchRule entities_should_be_in_entity_package = classes()
            .that()
            .areAnnotatedWith("io.jmix.core.entity.annotation.JmixEntity")
            .or()
            .areAnnotatedWith("jakarta.persistence.Entity")
            .should()
            .resideInAPackage("..entity..")
            .because("Entities must be located in entity package for consistency");

    @ArchTest
    @SuppressWarnings({"Checkstyle:ConstantName", "PMD.FieldNamingConventions", "PMD.LooseCoupling"})
    // ArchUnit convention uses snake_case for rule names, which conflicts with Java naming
    // LooseCoupling: ArchRule uses generic types from framework
    static final ArchRule no_cycles_between_packages =
            com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices()
                    .matching("com.digtp.start.(*)..")
                    .should()
                    .beFreeOfCycles()
                    .because("Cyclic dependencies between packages indicate architectural problems");

    @Test
    @SuppressWarnings("PMD.LooseCoupling") // ArchUnit JavaClasses is a framework type, not a standard collection
    void architectureRulesAreDefined() {
        // This test ensures ArchUnit rules are loaded and executed
        // Individual rules are tested via @ArchTest annotations above
        final JavaClasses classes = new ClassFileImporter().importPackages("com.digtp.start");
        // PMD: UseCollectionIsEmpty - isEmpty() not available on JavaClasses, size() check is appropriate
        Assertions.assertFalse(classes.isEmpty(), "No classes found for architecture testing");
    }
}
