package com.example.tp_library

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class ArchitectureTest {
    private  val basePackage = "com.example.tp_library"

    companion object {
        private lateinit var classes: JavaClasses
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
                "javax.."
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