# Changelog

All notable changes to this project are documented here. The format is based on
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.0]

### Added
- Initial release of the Spring Boot starter template.
- Layered, package-by-feature architecture (`auth`, `task`, `user`, `common`).
- Stateless JWT authentication and BCrypt password hashing.
- CRUD REST API for tasks with per-user ownership scoping and pagination.
- Bean Validation on all request DTOs with a consistent `ApiError` response contract.
- Global exception handling.
- MySQL persistence with Flyway migrations; Hibernate schema validation.
- MapStruct entity/DTO mapping.
- OpenAPI 3 documentation via springdoc (Swagger UI).
- Spring Boot Actuator health/info/metrics endpoints.
- Unit tests (JUnit 5, Mockito) and Testcontainers-backed integration tests.
- Multi-stage Dockerfile and docker-compose stack.
- GitHub Actions CI (build, test, Docker image build).
