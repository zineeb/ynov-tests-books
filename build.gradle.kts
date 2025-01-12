plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.1.6"
    id("io.spring.dependency-management") version "1.1.6"
    jacoco
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

sourceSets {
    create("testIntegration") {
        kotlin.srcDirs("src/testIntegration/kotlin")
        resources.srcDirs("src/testIntegration/resources")
        // Donner accès aux outputs du main et du test standard
        compileClasspath += sourceSets["main"].output + sourceSets["test"].output
        runtimeClasspath += sourceSets["main"].output + sourceSets["test"].output
    }
    test {
        kotlin.srcDirs("src/test/kotlin")
    }
}

configurations {
    val testIntegrationImplementation by getting {
        extendsFrom(configurations["implementation"])
    }
    val testIntegrationRuntimeOnly by getting {
        extendsFrom(configurations["runtimeOnly"])
    }
}

dependencies {
    // --- Dépendances principales (main)
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.liquibase:liquibase-core")
    runtimeOnly("org.postgresql:postgresql")

    // --- Dépendances pour les tests unitaires (test)
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.kotest:kotest-property:5.9.1")

    // --- Dépendances pour les tests d'intégration (testIntegration)
    add("testIntegrationImplementation", "org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    add("testIntegrationImplementation", "io.mockk:mockk:1.13.13")
    add("testIntegrationImplementation", "com.ninja-squad:springmockk:4.0.2")

    // --- Testcontainers pour PostgreSQL et JUnit Jupiter
    add("testIntegrationImplementation", "org.testcontainers:postgresql:1.19.0")
    add("testIntegrationImplementation", "org.testcontainers:junit-jupiter:1.19.0")
    add("testIntegrationImplementation", "org.testcontainers:jdbc:1.19.0")
}


tasks.register<Test>("testIntegration") {
    description = "Runs integration tests in src/testIntegration/kotlin"
    group = "verification"
    testClassesDirs = sourceSets["testIntegration"].output.classesDirs
    classpath = sourceSets["testIntegration"].runtimeClasspath
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.10"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test, tasks.named("testIntegration"))
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
    violationRules {
        rule {
            element = "CLASS"
            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

tasks.build {
    dependsOn("testIntegration")
}
