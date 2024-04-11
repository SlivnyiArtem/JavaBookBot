package ru.urfu.bot;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.Test;

public class ArchitectureTest {

    private static final String BASE_PACKAGE = "ru.urfu.bot";

    private static final JavaClasses CLASSES = new ClassFileImporter().importPackages(BASE_PACKAGE);

    @Test
    void layersTest() {
        Architectures.layeredArchitecture().consideringOnlyDependenciesInLayers()
                .layer("domain").definedBy(BASE_PACKAGE + ".domain..")
                .layer("app").definedBy(BASE_PACKAGE + ".app..")
                .layer("extern").definedBy(BASE_PACKAGE + ".infrastructure..")
                .whereLayer("app").mayOnlyBeAccessedByLayers("app", "extern")
                .whereLayer("extern").mayOnlyBeAccessedByLayers("extern")
                .check(CLASSES);
    }
}
