import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    id("org.springframework.boot") version "3.1.6"
    id("io.spring.dependency-management") version "1.1.6"
    id("jacoco")
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

repositories {
    mavenCentral()
}

sourceSets {
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
    create("testIntegration") {
        kotlin.srcDirs("src/testIntegration/kotlin")
        resources.srcDirs("src/testIntegration/resources")
        compileClasspath += sourceSets["main"].output + sourceSets["test"].output
        runtimeClasspath += sourceSets["main"].output + sourceSets["test"].output
    }
    test {
        kotlin.srcDirs("src/test/kotlin")
    }
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

    // Dépendances pour les tests d'architecture
    add("testArchitectureImplementation", "com.tngtech.archunit:archunit-junit5:1.0.1")
    add("testArchitectureImplementation", "io.kotest:kotest-assertions-core:5.9.1")
    add("testArchitectureImplementation", "io.kotest:kotest-runner-junit5:5.9.1")
    add("testArchitectureImplementation", "org.junit.jupiter:junit-jupiter-api:5.9.1")
    add("testArchitectureRuntimeOnly", "org.junit.jupiter:junit-jupiter-engine:5.9.1")
}

configurations.all {
    resolutionStrategy.force(
        "org.jetbrains.kotlin:kotlin-stdlib:1.9.0",
        "org.jetbrains.kotlin:kotlin-stdlib-common:1.9.0",
        "org.jetbrains.kotlin:kotlin-reflect:1.9.0",
        "org.jetbrains.kotlin:kotlin-compiler-embeddable:1.9.0"
    )
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
    description = "Runs architecture tests"
    group = "verification"
    testClassesDirs = sourceSets["testArchitecture"].output.classesDirs
    classpath = sourceSets["testArchitecture"].runtimeClasspath
    useJUnitPlatform()
}

tasks.test {
    useJUnitPlatform()
}

detekt {
    toolVersion = "1.23.1"
    config.setFrom(files("$projectDir/config/detekt.yml"))  // Assure-toi que le chemin est correct
    buildUponDefaultConfig = true
    source = files("src/main/kotlin", "src/test/kotlin")
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
    jvmTarget = "17"
    reports {
        xml.required.set(true)
        html.required.set(true)
        txt.required.set(false)
        sarif.required.set(false)
        md.required.set(false)
    }
}

tasks.build {
    dependsOn("testIntegration", "testComponent", "testArchitecture")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.10"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test, tasks.named("testIntegration"), tasks.named("testComponent"), tasks.named("testArchitecture"))
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
