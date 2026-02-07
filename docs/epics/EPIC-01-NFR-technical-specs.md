# Non-Functional Requirements (NFR) - Product Catalog Service

This document outlines the operational and evolutionary quality attributes required for the Product Catalog Service. These requirements provide the technical guardrails for the system's architecture and implementation.

## Technical Architecture & Design

* **[NFR-ARCH-01] Resource Efficiency:** The service shall leverage the **Quarkus** framework (JVM mode) to maintain a heap memory footprint below 256MB under idle state, ensuring high density in containerized environments.
* **[NFR-ARCH-02] Transactional Integrity:** All state changes must persist in a **PostgreSQL** relational database. The system must enforce **ACID** properties at the database level to prevent data corruption.
* **[NFR-ARCH-03] Interface Contract:** The system shall expose a **RESTful API** adhering to the Richardson Maturity Model Level 2, utilizing JSON (RFC 8259) and standard HTTP status codes ($2xx, 4xx, 5xx$).
* **[NFR-ARCH-04] Evolutionary API:** To ensure zero-downtime migrations, the API must implement **URI versioning** (e.g., `/api/v1/...`). Deprecation of older versions must follow a "sunset" header policy.

## Security & Data Guardrails

* **[NFR-SEC-01] Schema Validation:** Every entry point must perform **strict input validation** using Jakarta Bean Validation. Invalid payloads (e.g., negative prices, malformed SKUs) must be rejected before reaching the Domain layer.
* **[NFR-SEC-02] Data Consistency:** The system must implement **Optimistic Concurrency Control** (using `@Version` or similar) to detect and prevent "Lost Updates" during high-frequency stock modifications.
* **[NFR-SEC-03] Defensive Error Handling:** The API must implement a **Global Exception Mapping** strategy. Internal stack traces or SQL schema details must never be exposed to the client; only sanitized, correlation-indexed error messages are permitted.

## ⚡ Reliability & Performance

* **[NFR-RELY-01] Latency Threshold:** The system must achieve a **P95 latency of < 100ms** for read operations (GET) under a nominal load of 50 Requests Per Second (RPS).
* **[NFR-RELY-02] Horizontal Elasticity:** The application tier must be strictly **stateless**. Session data or local caches that prevent instance replication are prohibited to allow seamless scaling via Traefik/Kubernetes.
* **[NFR-RELY-03] Fault Tolerance:** The system shall remain responsive to read requests even during intensive write-locks on unrelated tables, utilizing appropriate database isolation levels (Read Committed).

## Quality & Observability

* **[NFR-QA-01] BDD Specification:** 100% of business use cases must be documented and verified via **Cucumber/Gherkin** features, acting as the "Single Source of Truth" for technical and functional stakeholders.
* **[NFR-QA-02] Environment Parity:** Integration tests must utilize **Testcontainers** to run against a real PostgreSQL instance, ensuring that the test environment matches the production runtime behavior.
* **[NFR-QA-03] Diagnostic Context:** Logs must be structured (JSON format preferred) and include **Request IDs** to allow trace-aggregation. **Health Check** endpoints (`/q/health/live` and `/q/health/ready`) must be exposed for orchestrator monitoring.

> Compliance with these NFRs is verified during the build process via ArchUnit tests and automated performance benchmarks.
