# Spring Boot Starter Template

A production-oriented **Spring Boot 3 + Java 21** starter template that demonstrates clean,
modular backend engineering practices. It ships a small but realistic REST API (JWT auth +
task management) wired up the way a real service would be: layered architecture, database
migrations, validation, consistent error handling, OpenAPI docs, containerization, a full
test suite, and CI.

Use it as a reference, or fork it as the foundation for your next service.

> **Note:** This is a *project template* (a scaffold you copy), not a Spring Boot
> auto-configuration “starter” dependency.

---

## Badges


![CI](https://github.com/Ahsan Khan/spring-boot-starter-template/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-brightgreen)
![License: MIT](https://img.shields.io/badge/License-MIT-blue)

---

## Table of contents

- [Features](#features)
- [Tech stack](#tech-stack)
- [Architecture](#architecture)
- [Getting started](#getting-started)
- [API overview](#api-overview)
- [Configuration](#configuration)
- [Testing](#testing)
- [Project structure](#project-structure)
- [Design decisions](#design-decisions)
- [Reusing this template](#reusing-this-template)
- [License](#license)

---

## Features

- **Stateless JWT authentication** — register/login endpoints, BCrypt password hashing,
  a `OncePerRequestFilter` that populates the security context, and method-level security.
- **Layered, package-by-feature architecture** — `auth`, `task`, `user`, and shared `common`.
- **DTO boundary** — entities never leak into the API; mapping is compile-time via **MapStruct**.
- **Bean Validation** on every request payload with a single, consistent `ApiError` contract.
- **Global exception handling** — no stack traces or internals leak to clients.
- **Versioned database migrations** with **Flyway**; Hibernate runs in `validate` mode.
- **Optimistic locking** and JPA auditing (`createdAt` / `updatedAt` / `version`) on all entities.
- **Pagination & filtering** with a serialization-stable page envelope.
- **OpenAPI 3 / Swagger UI** with a bearer-auth scheme.
- **Observability** via Spring Boot Actuator (health probes, metrics, Prometheus endpoint).
- **Testing** — unit tests (JUnit 5 + Mockito) and integration tests against a real MySQL
  using **Testcontainers**.
- **Containerized** — multi-stage, non-root **Dockerfile** and a **docker-compose** dev stack.
- **CI** — GitHub Actions building, testing, and building the Docker image.

---

## Tech stack

| Concern            | Choice                                             |
|--------------------|----------------------------------------------------|
| Language / runtime | Java 21 (LTS)                                       |
| Framework          | Spring Boot 3.4 (Web, Data JPA, Security, Actuator) |
| Build              | Maven (wrapper included)                            |
| Database           | MySQL 8                                             |
| Migrations         | Flyway                                              |
| Mapping            | MapStruct                                           |
| Auth               | JWT (jjwt) + BCrypt                                 |
| API docs           | springdoc-openapi (Swagger UI)                      |
| Testing            | JUnit 5, Mockito, Testcontainers, Spring Security Test |
| Container / CI     | Docker (multi-stage), GitHub Actions               |

---

## Architecture

Requests flow through clear, single-responsibility layers:

```
HTTP ──> Controller ──> Service ──> Repository ──> MySQL
              │             │
          DTO in/out    domain rules
              │         (ownership, tx)
          MapStruct
```

- **Controllers** handle HTTP concerns only (status codes, validation, `@AuthenticationPrincipal`).
- **Services** own transactions and business rules — most importantly, **ownership scoping**:
  a user can only ever read or mutate their own tasks.
- **Repositories** are Spring Data JPA interfaces.
- **Security** is a stateless JWT filter chain; failures return JSON `ApiError` responses (401/403).
- **Cross-cutting** concerns (error handling, base entity, page envelope) live under `common`.

---

## Getting started

### Prerequisites

- **JDK 21+**
- **Docker** (for the compose stack and integration tests)

### Option A — run everything with Docker Compose (recommended)

```bash
cp .env.example .env         # optional: tweak values
docker compose up --build
```

This starts MySQL and the app with demo data seeding enabled. Then open:

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- Health: <http://localhost:8080/actuator/health>

Demo credentials (seeded): `admin@example.com` / `admin12345`.

### Option B — run the app locally against a MySQL container

```bash
# 1. Start just the database
docker compose up -d db

# 2. Run the app (defaults already point at localhost:3306)
./mvnw spring-boot:run
```

### Quick API walkthrough

```bash
# Register (returns a JWT)
curl -s -X POST http://localhost:8080/api/v1/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"email":"jane@example.com","password":"S3curePass!","fullName":"Jane Doe"}'

# Save the accessToken from the response, then create a task
TOKEN=... # paste accessToken
curl -s -X POST http://localhost:8080/api/v1/tasks \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"title":"Ship the template","dueDate":"2026-08-01"}'

# List your tasks
curl -s http://localhost:8080/api/v1/tasks -H "Authorization: Bearer $TOKEN"
```

---

## API overview

Base path: `/api/v1`

| Method | Endpoint          | Auth | Description                              |
|--------|-------------------|------|------------------------------------------|
| POST   | `/auth/register`  | —    | Create an account, receive a JWT         |
| POST   | `/auth/login`     | —    | Authenticate, receive a JWT              |
| GET    | `/tasks`          | ✅   | List your tasks (paged, `?status=` filter) |
| POST   | `/tasks`          | ✅   | Create a task                            |
| GET    | `/tasks/{id}`     | ✅   | Get one of your tasks                    |
| PUT    | `/tasks/{id}`     | ✅   | Update one of your tasks                 |
| DELETE | `/tasks/{id}`     | ✅   | Delete one of your tasks                 |

Pagination query params: `page`, `size`, `sort` (e.g. `?page=0&size=20&sort=dueDate,asc`).

Full, interactive documentation is available at `/swagger-ui.html`.

---

## Configuration

All settings are externalized and overridable via environment variables (see
[`.env.example`](.env.example)). Key ones:

| Variable                 | Default        | Description                                         |
|--------------------------|----------------|-----------------------------------------------------|
| `DB_HOST` / `DB_PORT`    | `localhost` / `3306` | MySQL location                                |
| `DB_NAME`                | `taskdb`       | Database name                                       |
| `DB_USERNAME` / `DB_PASSWORD` | `taskuser` / `taskpass` | Credentials                            |
| `JWT_SECRET`             | dev value      | **Base64 HMAC secret, ≥ 256 bits — override in prod** |
| `JWT_EXPIRATION_MINUTES` | `60`           | Access-token lifetime                               |
| `CORS_ALLOWED_ORIGINS`   | `http://localhost:3000` | Allowed CORS origins                       |
| `SEED_ENABLED`           | `false`        | Seed a demo admin + tasks on startup                |

> **Security:** the default `JWT_SECRET` is for local development only. Generate a real one
> with `openssl rand -base64 48` and inject it via your secret manager in production.

---

## Testing

```bash
# Unit tests only (fast, no Docker needed)
./mvnw test

# Full suite incl. Testcontainers integration tests (requires Docker running)
./mvnw verify
```

- **Unit tests** (`*Test`) cover services and the JWT logic with Mockito — no Spring context.
- **Integration tests** (`*IT`) boot the full context against a **real MySQL** via Testcontainers
  and exercise the API end-to-end through `MockMvc`. They auto-skip when Docker is unavailable,
  so a machine without Docker still gets a green unit-test build.

---

## Project structure

```
src/main/java/com/example/taskmanager
├── TaskManagerApplication.java
├── auth/            # registration + login (controller, service, DTOs)
├── task/            # task feature (entity, repo, service, controller, mapper, DTOs)
├── user/            # user entity, role, repository
├── security/        # JWT service, filter, UserDetails adapter, entry points
├── config/          # security, OpenAPI, JPA auditing, CORS, dev seeder
└── common/          # base entity, shared DTOs, global error handling

src/main/resources
├── application.yml
└── db/migration/    # Flyway migrations (V1__init_schema.sql, …)

src/test/java/com/example/taskmanager
├── support/         # AbstractIntegrationTest (Testcontainers base)
├── *Test.java       # unit tests
└── *IT.java         # integration tests
```

---

## Design decisions

A few choices worth calling out (the kind a reviewer might ask about):

- **Package by feature, not by layer.** Scales better than `controllers/`, `services/`, `repositories/`
  packages as the codebase grows; related code lives together.
- **Entities stay framework-agnostic.** `User` is a plain JPA entity; the Spring Security
  `UserDetails` contract is satisfied by a separate `AppUserDetails` adapter.
- **Flyway owns the schema; Hibernate only validates.** This prevents drift and makes schema
  changes reviewable, versioned, and reproducible — `ddl-auto=validate`, never `update`.
- **Ownership enforced in the service layer.** Accessing another user's task returns `404`, not
  `403`, so the API doesn't leak the existence of resources you don't own.
- **Stable pagination envelope.** We return a purpose-built `PageResponse` rather than Spring
  Data's `Page`, whose JSON shape is not a guaranteed contract.
- **One error shape everywhere.** Every non-2xx response is an `ApiError`, including auth failures
  produced inside the security filter chain.

---

## Reusing this template

1. **Rename the base package** `com.example.taskmanager` to your own (e.g. `dev.yourname.service`).
2. Update `groupId` / `artifactId` in [`pom.xml`](pom.xml).
3. Replace the `task`/`user` example domain with yours; keep the `common`, `security`, and `config`
   scaffolding.
4. Add Flyway migrations for your schema; update entities accordingly.
5. Swap the `your-username` placeholders (badges, OpenAPI contact) for your GitHub handle.
6. Rotate `JWT_SECRET` and lock down `CORS_ALLOWED_ORIGINS` before deploying.

---

## License

Released under the [MIT License](LICENSE).
