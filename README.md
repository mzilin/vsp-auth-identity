# Video Streaming Platform - Auth Service


## Introduction

The `Auth Service` is an integral component of the `Video Streaming Platform`, responsible for user authentication as well as managing user passwords, passcodes, refresh tokens and reset tokens.

Built with Spring Boot, this microservice uses PostgreSQL as its database, providing a robust and scalable solution for data storage and manipulation.


## Technologies Used
This microservice utilises a comprehensive suite of technologies and dependencies, ensuring robust and scalable functionality:

- **Spring Boot** `3.2.5`:
    - **Actuator**: Monitors and manages the app.
    - **Data JPA**: Provides Java Persistence API for database integration.
    - **Security-Crypto**: Offers encryption and decryption functionalities for security.
    - **Web**: Supports web-based applications.

- **Spring Cloud** `2023.0.1`:
    - **Config**: Manages externalised configuration.
    - **Netflix Eureka Client**: Allows this microservice to register with a Eureka server.
    - **OpenFeign Client**: Enables easy creation of declarative REST clients that integrate with service discovery.

- **Java** `JDK 17`: Essential for secure, portable, high-performance software development.

- **Lombok**: Simplifies the codebase by reducing boilerplate.

- **Database Integration**:
    - **Flyway**: Manages database migrations.
    - **PostgreSQL**: Used for production databases.

- **Testing**:
    - **Mockito Core** `5.3.1`: Facilitates mock testing.


### Dependency Management

- **Gradle**: Automates build, test, and deployment processes.


### Containerization

- **Docker** (Optional): Automates OS-level virtualization on Windows and Linux.


## Requirements

To successfully set up and run the application, ensure you have the following installed:

- [Java JDK 17](https://www.oracle.com/uk/java/technologies/downloads/#java17)
- [Gradle](https://gradle.org/)
- [Docker](https://docs.docker.com/get-docker/) (optional)

Ensure that PostgreSQL is correctly set up and running, as it is required for the data storage.


## Installation

Follow these steps to get the Auth Service up and running:

1. Navigate into the app's directory
```shell
cd vsp-auth-service
```

2. Clean and build the microservice

```shell
./gradlew clean build
```

3. Start the microservice

```shell
./gradlew bootRun
```


## Testing

Ensure the application is working as expected by executing the unit tests:

```shell
./gradlew clean test
```


## License

This project is private and proprietary. Unauthorised copying, modification, distribution, or use of this software, via any medium, is strictly prohibited without explicit permission from the owner.


## Contact

For any questions or clarifications about the project, please reach out to the project owner via [www.mariuszilinskas.com](https://www.mariuszilinskas.com).

Marius Zilinskas

------

###### All rights are reserved. - Marius Zilinskas, 2024 to present