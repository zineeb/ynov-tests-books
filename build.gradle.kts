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
    toolchain { languageVersion = JavaLanguageVersion.of(17) }
}

repositories {
    mavenCentral()
}

sourceSets {
    create("testIntegration") {
        kotlin.srcDirs("src/testIntegration/kotlin")
        resources.srcDirs("src/testIntegration/resources")
        compileClasspath += sourceSets["main"].output + sourceSets["test"].output
        runtimeClasspath += sourceSets["main"].output + sourceSets["test"].output
    }
    create("testComponent") {
        kotlin.srcDirs("src/testComponent/kotlin")
        resources.srcDirs("src/testComponent/resources")
        compileClasspath += sourceSets["main"].output + sourceSets["test"].output
        runtimeClasspath += sourceSets["main"].output + sourceSets["test"].output
    }
    create("testArchitecture") {
        kotlin.srcDirs("src/testArchitecture/kotlin")
        resources.srcDirs("src/testArchitecture/resources")
        compileClasspath += sourceSets["main"].output + sourceSets["test"].output
        runtimeClasspath += sourceSets["main"].output + sourceSets["test"].output
    }
    test { kotlin.srcDirs("src/test/kotlin") }
}

configurations {
    val testIntegrationImplementation by getting { extendsFrom(configurations["implementation"]) }
    val testIntegrationRuntimeOnly by getting { extendsFrom(configurations["runtimeOnly"]) }
    val testComponentImplementation by getting { extendsFrom(configurations["implementation"]) }
    val testComponentRuntimeOnly by getting { extendsFrom(configurations["runtimeOnly"]) }
    val testArchitectureImplementation by getting { extendsFrom(configurations["implementation"]) }
    val testArchitectureRuntimeOnly by getting { extendsFrom(configurations["runtimeOnly"]) }
}

dependencies {
    // Dépendances principales
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.liquibase:liquibase-core")
    runtimeOnly("org.postgresql:postgresql")

    // Tests unitaires
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.kotest:kotest-property:5.9.1")

    // Tests d'intégration
    add("testIntegrationImplementation", "org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    add("testIntegrationImplementation", "io.mockk:mockk:1.13.13")
    add("testIntegrationImplementation", "com.ninja-squad:springmockk:4.0.2")
    add("testIntegrationImplementation", "org.testcontainers:postgresql:1.19.0")
    add("testIntegrationImplementation", "org.testcontainers:junit-jupiter:1.19.0")
    add("testIntegrationImplementation", "org.testcontainers:jdbc:1.19.0")

    // Dépendances pour les tests de composants
    add("testComponentImplementation", "org.jetbrains.kotlin:kotlin-test")
    add("testComponentImplementation", "org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    add("testComponentImplementation", "io.cucumber:cucumber-java:7.14.0")
    add("testComponentImplementation", "io.cucumber:cucumber-spring:7.14.0")
    add("testComponentImplementation", "io.cucumber:cucumber-junit:7.14.0")
    add("testComponentImplementation", "io.cucumber:cucumber-junit-platform-engine:7.14.0")
    add("testComponentImplementation", "io.rest-assured:rest-assured:5.3.2")
    add("testComponentImplementation", "org.junit.platform:junit-platform-suite:1.10.0")
    add("testComponentImplementation", "org.testcontainers:postgresql:1.19.1")
    add("testComponentImplementation", "io.kotest:kotest-assertions-core:5.9.1")
    add("testComponentImplementation", "org.testcontainers:jdbc:1.19.0")

    // Dépendances pour les tests d'architecture (ajoutez-les si nécessaire dans votre config)
    add("testArchitectureImplementation", "com.tngtech.archunit:archunit-junit5:1.0.1")
    add("testArchitectureImplementation", "io.kotest:kotest-assertions-core:5.9.1")
    add("testArchitectureImplementation", "io.kotest:kotest-runner-junit5:5.9.1")
}

tasks.named<ProcessResources>("processTestComponentResources") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<Test>("testIntegration") {
    description = "Runs integration tests in src/testIntegration/kotlin"
    group = "verification"
    testClassesDirs = sourceSets["testIntegration"].output.classesDirs
    classpath = sourceSets["testIntegration"].runtimeClasspath
    useJUnitPlatform()
}

tasks.register<Test>("testComponent") {
    description = "Runs component tests in src/testComponent/kotlin"
    group = "verification"
    testClassesDirs = sourceSets["testComponent"].output.classesDirs
    classpath = sourceSets["testComponent"].runtimeClasspath
    useJUnitPlatform()
}

tasks.register<Test>("testArchitecture") {
    description = "Runs architecture tests in src/testArchitecture/kotlin"
    group = "verification"
    testClassesDirs = sourceSets["testArchitecture"].output.classesDirs
    classpath = sourceSets["testArchitecture"].runtimeClasspath
    useJUnitPlatform()
}

jacoco { toolVersion = "0.8.10" }

tasks.jacocoTestReport {
    dependsOn(tasks.test, tasks.named("testIntegration"), tasks.named("testComponent"))
    reports { xml.required.set(true); html.required.set(true) }
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
    dependsOn("testIntegration", "testComponent", "testArchitecture")
}
