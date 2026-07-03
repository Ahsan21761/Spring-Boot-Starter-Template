# Contributing

Thanks for your interest in improving this template! This document describes how to work in the codebase.

## Prerequisites

- JDK 21+
- Docker (for running integration tests locally)
- No local Maven install required — use the bundled wrapper (`./mvnw`).

## Development workflow

1. Fork and branch from `main` (`feat/…`, `fix/…`, `chore/…`).
2. Make your change with accompanying tests.
3. Run the full build locally:
   ```bash
   ./mvnw verify
   ```
4. Open a pull request. CI must be green before review.

## Coding conventions

- **Package by feature**, not by layer (`auth`, `task`, `user`), with shared code under `common`.
- Keep entities free of framework/security concerns; adapt at the boundary.
- Never return entities from controllers — map to DTOs.
- All configuration is externalized via `@ConfigurationProperties`; no magic strings.
- Follow the existing formatting (see `.editorconfig`).

## Database changes

- The schema is owned by **Flyway**. Add a **new** migration
  (`src/main/resources/db/migration/V<n>__description.sql`).
- **Never edit a migration that has already been merged** — migrations are immutable.
- Update the corresponding JPA entity so Hibernate's `validate` check still passes.

## Commit messages

Use [Conventional Commits](https://www.conventionalcommits.org/) where practical:
`feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`.
