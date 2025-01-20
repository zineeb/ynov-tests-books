# Library Management - Kotlin Project

This project is a Spring Boot application in Kotlin for managing books. It allows adding, listing, and reserving books while enforcing domain rules. The project also includes unit tests, integration tests, and component tests using Kotest, MockK, Spring MockMVC, Cucumber, and Testcontainers.

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation and Running](#installation-and-running)
- [Tests](#tests)
- [Docker and Testcontainers Configuration](#docker-and-testcontainers-configuration)
    - [On Windows (Docker Desktop)](#on-windows-docker-desktop)
    - [On Linux](#on-linux)
    - [On macOS](#on-macos)
- [Warnings and Cleanup](#warnings-and-cleanup)

## Features

- **Add and display books**: Add books and retrieve a list, including their reservation status.
- **Book reservation**: Reserve a book. A book that is already reserved cannot be reserved again.
- **Domain rule validation**: Custom exceptions for rule violations (e.g., reserving an already reserved book).

## Architecture

The project follows a hexagonal architecture:

- **Domain**: Contains entities (`Book`), ports (`BookRepository`), and use cases (`BookCase`, `BookDomainException`).
- **Infrastructure**:
    - **Driving Adapters**: REST controllers (`BookController`, `GlobalExceptionHandler`, `BookDTO`).
    - **Driven Adapters**: Implementations of ports, such as `BookDAO` for PostgreSQL.
    - **Application Configuration**: Spring configuration to define beans and dependencies.
- **Tests**:
    - **Unit Tests** with Kotest and MockK.
    - **Web Integration Tests** with Spring MockMVC.
    - **Database Integration Tests** with Testcontainers.
    - **Component Tests** with Cucumber and Testcontainers.

## Prerequisites

- [JDK 17](https://adoptium.net/) or higher
- [Docker](https://www.docker.com/get-started) (for integration tests with Testcontainers)
- [Gradle](https://gradle.org/install/) (or use the included Gradle wrapper)

## Installation and Running

1. **Clone the repository**:

   ```bash
   git clone https://your-repository-url.git
   cd tp_library
   ```

2. **Build the project and run tests**:

   ```bash
   ./gradlew clean build
   ```

   This command compiles the project, runs all tests (unit, integration, component), and performs static analysis with Detekt.

3. **Run the application**:

   ```bash
   ./gradlew bootRun
   ```

   The application will start at [http://localhost:8080](http://localhost:8080).

## Tests

To run specific tests:

- **Unit Tests**: Executed by default with `./gradlew test`.
- **Integration Tests**:
    - Use `./gradlew testIntegration` to run integration tests.
- **Component Tests**:
    - Use `./gradlew testComponent` to run Cucumber component tests.
- **Architecture Tests**:
    - Use `./gradlew testArchitecture` to verify architectural rules.

Test and coverage reports are generated in the `build/reports/` directory.

## Docker and Testcontainers Configuration

The project uses **Testcontainers** to start a PostgreSQL container during the execution of integration and component tests. Docker must be installed and running for these tests to work. Here's how to configure Docker based on your operating system:

### On Windows (Docker Desktop)

1. **Download and install Docker Desktop** from [https://www.docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop).

2. **Launch Docker Desktop** and ensure it is running. A Docker icon should appear in the taskbar.

3. **Verify Docker functionality**:
    - Open Command Prompt or PowerShell.
    - Run `docker ps`. If Docker is working, this command will list running containers.

4. With Docker Desktop active, run `./gradlew clean build`. Testcontainers will then start PostgreSQL containers for your tests.

### On Linux

1. **Install Docker** (for Debian/Ubuntu):

   ```bash
   sudo apt-get update
   sudo apt-get install docker.io
   ```

   For other distributions, refer to the [Docker documentation for Linux](https://docs.docker.com/engine/install/#server).

2. **Start and enable the Docker service**:

   ```bash
   sudo systemctl start docker
   sudo systemctl enable docker
   ```

3. **Verify Docker functionality**:

   ```bash
   sudo docker run hello-world
   ```

   This command downloads and runs a test image to verify that Docker is operational.

4. (Optional) To avoid using `sudo` with Docker commands, add your user to the `docker` group:

   ```bash
   sudo usermod -aG docker $USER
   ```

   Then log out and log back in to apply the changes.

5. With Docker running, execute `./gradlew clean build` to build the project and run tests.

### On macOS

1. **Download and install Docker Desktop** from [https://www.docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop).

2. **Launch Docker Desktop**. Ensure it is running (Docker icon should appear in the menu bar).

3. **Verify Docker functionality**:
    - Open Terminal.
    - Run `docker ps`. If Docker is working, you'll see a list of running containers.

4. With Docker Desktop active, run `./gradlew clean build`. Testcontainers will start containers for the tests.

## Warnings and Cleanup

- **Testcontainers** uses ephemeral Docker containers for tests. After tests, these containers are automatically stopped and removed.
- If a container fails to start, check your Docker installation and ensure your user has the necessary permissions.
- **Detekt** reports code style issues. Follow its recommendations to improve code quality.
