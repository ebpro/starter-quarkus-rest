# Product Catalog — Quarkus REST Starter

> **Lecture context:** This project is the **final example** (`V5`) from the
> [Java REST Quarkus lecture notebook](https://github.com/ebpro/notebook-java-rest-sample-quarkus).
> It consolidates the full V1 → V5 architectural progression into a single,
> production-quality codebase. Read the notebook first to understand how and
> why each design decision was made.

A RESTful Product Catalog built with **Quarkus 3**, **PostgreSQL**, and **Java 21**,
demonstrating layered architecture, BDD testing, OpenAPI documentation, and
container-based CI/CD.

---

## Architecture at a Glance

```
api/            ← JAX-RS resource, OpenAPI docs, exception mappers
application/    ← ProductService (framework-free business logic), DTOs
domain/         ← Product record (immutable, self-validating)
infrastructure/ ← JPA entity, Panache repository, mapper, REST client
```

Architectural constraints are enforced automatically by **ArchUnit** tests
(domain isolation, layer dependencies, naming conventions).

---

## Quick Start — Dev Mode (recommended)

**Prerequisites:** Java 21+, Docker.

```bash
# 1. Start a local PostgreSQL instance
export DB_NAME=products
export DB_USER=tpuser
export DB_PASSWORD=Tp@2026
export DB_HOST=localhost
export DB_PORT=15432
export HOST_POSTGRES_PORT=15432
export HOST_HTTP_PORT=8080

docker run --name product-catalog-postgres \
    -e POSTGRES_DB=$DB_NAME \
    -e POSTGRES_USER=$DB_USER \
    -e POSTGRES_PASSWORD=$DB_PASSWORD \
    -p $HOST_POSTGRES_PORT:5432 \
    -d postgres:16-alpine

# 2. Run the application in Quarkus dev mode (hot reload)
./mvnw quarkus:dev
```

| Endpoint      | URL                                         |
|---------------|---------------------------------------------|
| REST API      | http://localhost:8080/api/v1/products       |
| Swagger UI    | http://localhost:8080/q/swagger-ui          |
| Dev console   | http://localhost:8080/q/dev-ui              |
| Health        | http://localhost:8080/q/health              |

Try the bundled HTTP requests in [`products.http`](products.http) from your IDE.

## Running Tests

No database setup needed — [Testcontainers](https://testcontainers.com/) spins
up a real PostgreSQL instance automatically.

```bash
# Unit tests + integration tests + BDD acceptance tests + architecture tests
./mvnw clean verify
```

| Type              | Location                                         | Runs with    |
|-------------------|--------------------------------------------------|--------------|
| Unit (service)    | `src/test/…/application/ProductServiceTest.java` | `mvn test`   |
| Architecture      | `src/test/…/architecture/`                       | `mvn test`   |
| BDD acceptance    | `src/test/resources/features/catalog/`           | `mvn verify` |
| Technical specs   | `src/test/…/TechnicalSpecsIT.java`               | `mvn verify` |

## Living Documentation

```bash
# Build AsciiDoc + UML living documentation
./mvnw -Pdocs -DskipTests prepare-package
```

Generated output:

- `target/living-docs/living-doc.html`

## Run the Full Stack with Docker Compose

```bash
# Minimal stack: Postgres + application
docker compose up -d

# Verify
curl http://localhost:8080/api/v1/products
```

Copy `.env.example` to `.env` to override default environment variables
(image tag, ports, credentials).

## Build a Container Image

```bash
# Build and tag a JVM image locally via Jib (no Dockerfile needed)
./mvnw package -Pjvm -DskipTests
```

## Build a Native Image

```bash
# Build a native executable via GraalVM
./mvnw package -Pnative \
    -DskipTests \
    -Dquarkus.container-image.tag=1.0.0-native
```
