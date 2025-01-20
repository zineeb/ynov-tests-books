package com.example.tplibrary

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.junit.jupiter.api.*

/**
 * Architecture tests verifying package dependency rules.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArchitectureTest {
    private val basePackage = "com.example.tplibrary"
    private lateinit var classes: JavaClasses

    @BeforeAll
    fun setup() {
        classes = ClassFileImporter().importPackages(basePackage)
    }

    @Test
    fun `domain classes must not be infrastructure-dependent`() {
        val rule = classes()
            .that().resideInAPackage("$basePackage.domain..")
            .should().onlyDependOnClassesThat().resideOutsideOfPackage("$basePackage.infrastructure")
        rule.check(classes)
    }

    @Test
    fun `controller classes must depend only on the domain and Spring`() {
        val rule = classes()
            .that().resideInAPackage("$basePackage.infrastructure.driving.controller..")
            .should().onlyDependOnClassesThat().resideInAnyPackage(
                "$basePackage.domain..",
                "$basePackage.infrastructure.driving.controller..",
                "org.springframework..",
                "java..",
                "javax..",
                "kotlin..",
                "org.jetbrains.annotations.."
            )
        rule.check(classes)
    }

    @Test
    fun `driven classes must be domain-dependent`() {
        val rule = classes()
            .that().resideInAPackage("$basePackage.infrastructure.driven..")
            .should().dependOnClassesThat().resideInAPackage("$basePackage.domain..")
        rule.check(classes)
    }
}
