package fr.insee.pearljam.config;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.base.DescribedPredicate;
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
    void domainShouldNotDependOnApiPackages() {
        noClasses()
                .that().resideInAPackage("fr.insee.pearljam.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "fr.insee.pearljam.api.."
                )
                .check(importedClasses);
    }

    /**
     * Allow entities for now as the code is not clean enough
     */
    @Test
    void domainShouldNotDependOnInfrastructurePackagesExceptEntities() {
        noClasses()
                .that().resideInAPackage("fr.insee.pearljam.domain..")
                .should().dependOnClassesThat(new DescribedPredicate<>("infrastructure classes except persistence entities") {
                    @Override
                    public boolean test(JavaClass javaClass) {
                        String packageName = javaClass.getPackageName();
                        boolean isInfrastructure = packageName.startsWith("fr.insee.pearljam.infrastructure.");
                        boolean isPersistence = packageName.contains(".persistence.");
                        boolean isEntityPackage = packageName.contains(".entity.") || packageName.endsWith(".entity");
                        boolean isAllowedEntity = isPersistence && isEntityPackage;
                        return isInfrastructure && !isAllowedEntity;
                    }
                })
                .check(importedClasses);
    }

    @Test
    void contractsShouldNotDependOnApiOrInfrastructurePackages() {
        noClasses()
                .that().resideInAPackage("fr.insee.pearljam.contracts..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "fr.insee.pearljam.api..",
                        "fr.insee.pearljam.infrastructure.."
                )
                .check(importedClasses);
    }

    @Test
    void infrastructureShouldNotDependOnApiPackages() {
        noClasses()
                .that().resideInAPackage("fr.insee.pearljam.infrastructure..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "fr.insee.pearljam.api.."
                )
                .check(importedClasses);
    }
}
