[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/projects/jdk/25/)
[![CI Pipeline](https://github.com/OpenDataJungle/OpenDataJungle-Reference-Data-Api/actions/workflows/ci.yml/badge.svg)](https://github.com/OpenDataJungle/OpenDataJungle-Reference-Data-Api/actions/workflows/ci.yml)

# OpenDataJungle Reference Data API

REST API for users, groups, and permissions — the shared identity/authorization reference data for the
[OpenDataJungle](https://www.opendatajungle.com) platform (alongside services like the
[Conversation API](https://github.com/OpenDataJungle/OpenDataJungle-Conversation-Api)
and [Knowledge API](https://github.com/OpenDataJungle/OpenDataJungle-Knowledge-Api)).

## Architecture

Hexagonal architecture (Ports & Adapters): `client` (REST controllers/DTOs) → `business` (domain services/models) →
`infra` (JPA persistence, configuration), enforced by an ArchUnit test.

## Features

- CRUD on **users**, **groups**, and **permissions** (`read`/`write`/`admin` flags)
- **Group membership**: attach/remove a user to a group with a given permission, list a user's groups or a group's
  members
- Pagination + filtering (by username / group name) on list endpoints
- OAuth2/JWT security, scope-based (`referencedata.read|write|delete|admin`)

## Tech stack

| Component | Technology                              |
|-----------|-----------------------------------------|
| Framework | Spring Boot 4                           |
| Language  | Java 25                                 |
| Database  | PostgreSQL                              |
| Security  | Spring Security, OAuth2 Resource Server |
| Testing   | JUnit 5, Testcontainers, ArchUnit       |
| Metrics   | Micrometer + Prometheus                 |

## Getting started

### Prerequisites

- JDK 25
- Docker (for the local PostgreSQL database and integration tests)
- An OAuth2/OIDC provider issuing JWTs (e.g. Keycloak) for authentication — not required in `local`/`test` profile,
  see [Security](#security)

### Run the database

```bash
cd infra/container
docker compose up -d
```

This starts a PostgreSQL instance on port `5432`.

### Run the application

```bash
./mvnw spring-boot:run
```

The API starts on `http://localhost:8083` by default.

### Run the tests

```bash
# Unit tests
./mvnw test

# Integration tests (requires Docker for Testcontainers)
./mvnw verify -Pit
```

## Configuration

Configuration lives in `src/main/resources/application.yml` and is overridable via environment variables.

#### Application & server

| Variable              | Description         | Default                             |
|-----------------------|---------------------|-------------------------------------|
| `APPLICATION_TITLE`   | Application title   | `OpenDataJungle Reference Data API` |
| `APPLICATION_VERSION` | Application version | `pom.xml` version                   |
| `SERVER_PORT`         | HTTP port           | `8083`                              |

#### Database

| Variable                                | Description            | Default                                             |
|-----------------------------------------|------------------------|-----------------------------------------------------|
| `DATABASE_URL`                          | PostgreSQL JDBC URL    | `jdbc:postgresql://localhost:5432/open_data_jungle` |
| `DATABASE_USERNAME`                     | PostgreSQL user        | `user`                                              |
| `DATABASE_PASSWORD`                     | PostgreSQL password    | `password`                                          |
| `DATABASE_DRIVER`                       | JDBC driver            | `org.postgresql.Driver`                             |
| `JPA_DDL_AUTO`                          | Hibernate DDL mode     | `none`                                              |
| `JPA_SHOW_SQL` / `HIBERNATE_FORMAT_SQL` | Log/format SQL queries | `false`                                             |
| `JPA_OPEN_IN_VIEW`                      | Open Session In View   | `false`                                             |

#### Security

| Variable                              | Description                            | Default                |
|---------------------------------------|----------------------------------------|------------------------|
| `SECURITY_SCOPE_REFERENCEDATA_READ`   | Scope to read users/groups/permissions | `referencedata.read`   |
| `SECURITY_SCOPE_REFERENCEDATA_WRITE`  | Scope to create/update                 | `referencedata.write`  |
| `SECURITY_SCOPE_REFERENCEDATA_DELETE` | Scope to delete                        | `referencedata.delete` |
| `SECURITY_SCOPE_REFERENCEDATA_ADMIN`  | Reserved admin scope                   | `referencedata.admin`  |

See `application.yml` for the full list.

> [!NOTE]
> Authentication (OAuth2/JWT), CORS, and error-handling are wired by the shared
> [`opendatajungle-commons`](https://github.com/OpenDataJungle/OpenDataJungle-Commons).<br>
> See its README for the corresponding configuration properties.

## Security

Endpoints require a JWT (OAuth2 Resource Server) with the scopes listed above.

Two Spring profiles disable authentication entirely and allow anonymous access—useful for standalone environments, local
development, and automated testing:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

- **`local`** — for running the API locally without an OAuth2 server.
- **`test`** — used automatically by the test suite (`@ActiveProfiles("test")`).

In production, no profile is active by default, so OAuth2/JWT authentication is enforced.

## API overview

| Method                    | Path                                                                 | Scope                  | Description                                |
|---------------------------|----------------------------------------------------------------------|------------------------|--------------------------------------------|
| `GET`                     | `/api/v1/users`                                                      | read                   | List users (paginated, filter by username) |
| `GET`                     | `/api/v1/users/{id}`                                                 | read                   | Get user by id                             |
| `GET`                     | `/api/v1/users/username/{username}`                                  | read                   | Get user by username                       |
| `POST`                    | `/api/v1/users/me`                                                   | read                   | Get or create current user                 |
| `POST` / `PUT` / `DELETE` | `/api/v1/users/{id}`                                                 | write / write / delete | Create / update / delete user              |
| `GET`                     | `/api/v1/groups`                                                     | read                   | List groups (paginated, filter by name)    |
| `GET`                     | `/api/v1/groups/{id}`                                                | read                   | Get group by id                            |
| `POST` / `PUT` / `DELETE` | `/api/v1/groups/{id}`                                                | write / write / delete | Create / update / delete group             |
| `GET`                     | `/api/v1/permissions`                                                | read                   | List permissions (paginated)               |
| `GET`                     | `/api/v1/permissions/{id}`                                           | read                   | Get permission by id                       |
| `POST` / `PUT` / `DELETE` | `/api/v1/permissions/{id}`                                           | write / write / delete | Create / update / delete permission        |
| `GET`                     | `/api/v1/users/{userId}/groups`                                      | read                   | List a user's groups                       |
| `GET`                     | `/api/v1/groups/{groupId}/users`                                     | read                   | List a group's members                     |
| `POST`                    | `/api/v1/groups/{groupId}/users/{userId}/permissions/{permissionId}` | write                  | Add user to group                          |
| `DELETE`                  | `/api/v1/groups/{groupId}/users/{userId}`                            | delete                 | Remove user from group                     |

The full OpenAPI 3 specification is available at [
`docs/OpenDataJungleReferenceDataAPI_Openapi.json`](docs/OpenDataJungleReferenceDataAPI_Openapi.json). It's regenerated
from the running application (`/v3/api-docs`) by the `OpenApiGenerationIT` integration test:


## Contributing

Issues and pull requests are welcome: https://github.com/OpenDataJungle/OpenDataJungle-Commons

## Contact

- **Website:** [www.opendatajungle.com](https://www.opendatajungle.com)
- **Email:** [contact@opendatajungle.com](mailto:contact@opendatajungle.com)
- **Organization:** [github.com/OpenDataJungle](https://github.com/OpenDataJungle)

## License

Licensed under the [GNU General Public License v3.0](LICENSE).
