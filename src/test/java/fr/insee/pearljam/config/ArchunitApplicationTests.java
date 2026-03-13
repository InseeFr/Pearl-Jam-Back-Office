package fr.insee.pearljam.config;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;


class ArchunitApplicationTests {

    private JavaClasses importedClasses;
    private Architectures.LayeredArchitecture architecture;

    @BeforeEach
    public void setup() {
        String projectPackage = "fr.insee.pearljam";
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(projectPackage);


        architecture = Architectures.layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("application").definedBy("..api..")
                .layer("old.service").definedBy("..api..service..")
                .layer("domain.port.userside").definedBy("..domain..port.userside..")
                .layer("domain.port.serverside").definedBy("..domain..port.serverside..")
                .layer("domain.model").definedBy("..domain..model..")
                .layer("domain.service").definedBy("..domain..service..")
                .layer("infrastructure").definedBy("..infrastructure..")
                .layer("infrastructure.adapter").definedBy("..infrastructure..adapter..");
    }

    @Test
    @Disabled("enable this after full refacto, fail for now")
    void presentationLayerShouldNotBeAccessedByOtherLayers() {
        architecture
                .whereLayer("application").mayNotBeAccessedByAnyLayer()
                .check(importedClasses);
    }

    @Test
    void serviceClassesShouldNotBeAccessedDirectly() {
        architecture
                .whereLayer("domain.service").mayNotBeAccessedByAnyLayer()
                .check(importedClasses);
    }

    @Test
    void usersidePortsShouldOnlyBeAccessedByControllerAndServices() {
        architecture
                .whereLayer("domain.port.userside").mayOnlyBeAccessedByLayers("domain.service", "application")
                .check(importedClasses);
    }

    @Test
    void serversidePortsShouldOnlyBeAccessedByDaoAndServices() {
        architecture
                .whereLayer("domain.port.serverside").mayOnlyBeAccessedByLayers("domain.service", "infrastructure.adapter", "old.service")
                .check(importedClasses);
    }

    @Test
    void modelsShouldBeAccessedByAllLayers() {
        architecture
                .whereLayer("domain.model").mayOnlyBeAccessedByLayers(
                        "domain.port.userside",
                        "domain.port.serverside",
                        "domain.service",
                        "infrastructure.adapter",
                        "application",
                        "infrastructure"
                )
                .check(importedClasses);
    }

    @Test
    void serviceClassesShouldOnlyAccessDomainClasses() {
        classes()
                .that().resideInAPackage("..domain..service..")
                .should().onlyAccessClassesThat().resideInAnyPackage("java..", "org.slf4j..", "..domain..")
                .check(importedClasses);
    }

    @Test
    void infrastructureClassesShouldNotBeAccessed() {
        classes()
                .that().resideOutsideOfPackage("..infrastructure.security.config.swagger")
                .and().resideOutsideOfPackage("..infrastructure.mail")
                .and().resideInAPackage("..infrastructure..")
                // TODO disable api.dto, api.domain, api.campaign.dto, api.surveyunit.dto when refacto is done
                .should().onlyBeAccessed().byClassesThat().resideInAnyPackage("..infrastructure..", "..api.dto..", "..api.domain..", "..api.service..", "..api.campaign..", "..api.surveyunit..")
                .check(importedClasses);
    }

    @Test
    void infrastructureSecurityClassesShouldOnlyBeAccessedByConfigurationApi() {
        classes()
                .that().resideInAPackage("..infrastructure.security.config.swagger")
                .should().onlyBeAccessed().byClassesThat().resideInAnyPackage("..api.configuration..", "..infrastructure..")
                .check(importedClasses);
    }

    // ===== Hexagonal Architecture target rules =====
    // These rules represent the end-state of the refactoring.
    // Enable them progressively as each aggregate is migrated.

    @Test
    void domainShouldNotDependOnInfrastructure() {
        noClasses()
                .that().resideInAPackage("fr.insee.pearljam.domain..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
                .check(importedClasses);
    }

    @Test
    @Disabled("enable after full refacto - domain still depends on api.domain enums")
    void domainShouldNotDependOnApi() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..api..")
                .check(importedClasses);
    }

    @Test
    @Disabled("enable after full refacto - infrastructure still references api.domain entities")
    void infrastructureShouldNotDependOnApi() {
        noClasses()
                .that().resideInAPackage("..infrastructure..")
                .should().dependOnClassesThat().resideInAPackage("..api..")
                .check(importedClasses);
    }
}