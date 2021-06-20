package com.emu.rule_engine_ms;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.emu.rule_engine_ms");

        noClasses()
            .that()
            .resideInAnyPackage("com.emu.rule_engine_ms.service..")
            .or()
            .resideInAnyPackage("com.emu.rule_engine_ms.repository..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..com.emu.rule_engine_ms.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses);
    }
}
