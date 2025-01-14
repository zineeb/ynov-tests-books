package com.example.tplibrary

import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Testcontainers
class CucumberSpringConfiguration {

    companion object {
        @JvmStatic
        @Container
        val postgreSQLContainer = PostgreSQLContainer<Nothing>("postgres:15.3-alpine")
            .apply {
                withDatabaseName("testdb")
                withUsername("testuser")
                withPassword("testpass")
            }

        @JvmStatic
        @DynamicPropertySource
        fun configureTestDatabase(registry: DynamicPropertyRegistry) {
            postgreSQLContainer.start()
            registry.add("spring.datasource.url") { postgreSQLContainer.jdbcUrl }
            registry.add("spring.datasource.username") { postgreSQLContainer.username }
            registry.add("spring.datasource.password") { postgreSQLContainer.password }
            registry.add("spring.liquibase.enabled") { "false" }
        }
    }
}
