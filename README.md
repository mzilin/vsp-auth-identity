# Video Streaming Platform - Auth Service


## Introduction

The `Auth Service` is an integral component of the `Video Streaming Platform`, responsible for user authentication as well as managing user passwords, passcodes, refresh tokens and reset tokens.

Built with Spring Boot, this microservice uses PostgreSQL as its database, providing a robust and scalable solution for data storage and manipulation.


## Technologies Used
This microservice utilises a comprehensive suite of technologies and dependencies, ensuring robust and scalable functionality:

- **Spring Boot** `3.2.5`:
  - **Actuator**: Monitors and manages the application, providing insights into runtime operations and health.
  - **RabbitMQ (AMQP)**: Facilitates robust, asynchronous message communication between microservices, enhancing scalability and decoupling components.
  - **Data JPA**: Simplifies database integration by managing relational data access in a more object-oriented manner using Java Persistence API.
  - **Security-Crypto**: Provides robust encryption and decryption functionalities to secure sensitive data, ensuring data privacy and integrity in applications.
  - **Web**: Enables building web-based applications and services, supporting RESTful endpoints and traditional MVC setups.

- **Spring Cloud** `2023.0.1`:
  - **Config**: Manages externalised configuration, allowing applications to fetch their settings from a centralized source.
  - **Netflix Eureka Client**: Enables the microservice to register with a Eureka server for service discovery.
  - **OpenFeign Client**: Simplifies building and managing declarative REST clients that automatically integrate with service discovery.

- **Java** `JDK 17`: Essential for secure, portable, high-performance software development.

- **Lombok**: Reduces boilerplate in Java code significantly, automating the generation of getters, setters, constructors, and other common methods.

- **Database Integration**:
  - **Flyway**: Manages and tracks database migrations, ensuring that database schema changes are version-controlled and consistently applied across various environments.
  - **PostgreSQL**: A robust, open-source object-relational database system widely used in production environments for its advanced features and proven architecture.

- **Testing**:
  - **Mockito Core** `5.3.1`: Provides essential mocking capabilities for unit testing, thereby facilitating thorough and effective test cases.


### Dependency Management

- **Gradle**: A powerful build automation tool that streamlines the compilation, testing, and deployment processes for software projects.


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