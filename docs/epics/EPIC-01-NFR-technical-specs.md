# Non-Functional Requirements (NFR)

## Technical Architecture

- [NFR-ARCH-01] Runtime Environment: The service must run on a cloud-native JVM stack (Quarkus) to ensure low memory footprint.
- [NFR-ARCH-02] Data Persistence: A relational database (PostgreSQL) is required to guarantee ACID compliance for all transactions.
- [NFR-ARCH-03] API Standard: The system must expose a RESTful interface using JSON for data exchange and standard HTTP status codes.
- [NFR-ARCH-04] Versioning Policy: The API must support backward compatibility through URI versioning to ensure service continuity.

## Security & Data Integrity

- [NFR-SEC-01] Input Sanitization: All incoming data must be validated (Unique SKU, positive prices, non-negative stock).
- [NFR-SEC-02] Concurrency Management: The system must prevent "Lost Update" scenarios during stock adjustments using appropriate locking.
- [NFR-SEC-03] Information Leakage: Sanitized error messages must be returned to avoid exposing internal stack traces or schema details.

## Reliability & Performance

- [NFR-RELY-01] Response Times: Under normal load, 95% of retrieval requests (GET) should respond in under 100ms.
- [NFR-RELY-02] Scalability: The service must be stateless to allow horizontal scaling across multiple container instances.
- [NFR-RELY-03] Availability: The catalog must remain retrievable even during heavy write operations (Creation/Deletion).

## Maintainability & Quality Assurance

- [NFR-QA-01] Living Documentation: All business rules must be backed by automated BDD (Behavior-Driven Development) specifications.
- [NFR-QA-02] Test Strategy: Core business flows must be tested against a production-equivalent database using integration tests.
- [NFR-QA-03] Observability: Structured logging (SLF4J) and Health Check endpoints must be provided for orchestration and monitoring.
