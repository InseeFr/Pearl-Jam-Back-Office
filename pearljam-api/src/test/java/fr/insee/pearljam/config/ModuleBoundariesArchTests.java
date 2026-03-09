package fr.insee.pearljam.config;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ModuleBoundariesArchTests {

    private JavaClasses importedClasses;

    @BeforeEach
    void setup() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("fr.insee.pearljam");
    }

    @Test
    void apiShouldNotDependOnJpaRepositories() {
        noClasses()
                .that().resideInAPackage("fr.insee.pearljam.api..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..infrastructure.persistence..jpa..",
                        "org.springframework.data.jpa.repository.."
                )
                .check(importedClasses);
    }

    @Test
    void apiShouldNotDependOnJakartaPersistence() {
        noClasses()
                .that().resideInAPackage("fr.insee.pearljam.api..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "jakarta.persistence.."
                )
                .check(importedClasses);
    }

    @Test
    void infrastructureAdaptersInApiModuleShouldNotDependOnApiPackage() {
        noClasses()
                .that().resideInAPackage("fr.insee.pearljam.infrastructure..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "fr.insee.pearljam.api.."
                )
                .check(importedClasses);
    }
}
