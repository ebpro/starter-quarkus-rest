@EPIC-01 @NFR @Security @Reliability
Feature: Catalog Robustness and Non-Functional Requirements
  In order to guarantee system stability, data integrity, and security
  As a system enforcing production-grade constraints
  I want invalid data to be rejected, internal details to stay hidden,
  and read operations to remain fast and isolated

  # ─────────────────────────────────────────────────────────────────────────
  # Background: each scenario starts with a clean, empty catalog
  # to ensure full test isolation (NFR-QA-02 — Environment Parity)
  # ─────────────────────────────────────────────────────────────────────────
  Background:
    Given the product catalog is empty

  # =========================================================================
  # NFR-SEC-01 — Schema Validation
  # Every entry point must enforce Jakarta Bean Validation
  # before business logic executes.
  # =========================================================================

  @NFR-SEC-01
  Scenario: [NFR-SEC-01] Reject a product with a negative price
    When the client creates a product with:
      | sku   | name     | price  | stock |
      | BAD-P | Cheap TV | -10.00 | 5     |
    Then the response status should be 400
    And the response body should contain a field "error"
    And the response body should not contain "exception"
    And the response body should not contain "stackTrace"

  @NFR-SEC-01
  Scenario: [NFR-SEC-01] Reject a product with a zero price
    When the client creates a product with:
      | sku   | name      | price | stock |
      | ZRO-P | Free TV   | 0.00  | 5     |
    Then the response status should be 400
    And the response body should contain a field "error"

  @NFR-SEC-01
  Scenario: [NFR-SEC-01] Reject a product with negative stock
    When the client creates a product with:
      | sku   | name     | price | stock |
      | STK-1 | Cheap TV | 10.00 | -5    |
    Then the response status should be 400
    And the response body should contain a field "error"
    And the response body should not contain "exception"

  @NFR-SEC-01
  Scenario: [NFR-SEC-01] Reject a product with a blank SKU
    When the client creates a product with:
      | sku | name      | price  | stock |
      |     | No SKU TV | 199.00 | 3     |
    Then the response status should be 400
    And the response body should contain a field "error"

  @NFR-SEC-01
  Scenario: [NFR-SEC-01] Reject a product with a blank name
    When the client creates a product with:
      | sku    | name | price  | stock |
      | NM-001 |      | 299.00 | 2     |
    Then the response status should be 400
    And the response body should contain a field "error"

  # =========================================================================
  # NFR-SEC-03 — Defensive Error Handling
  # Internal stack traces, SQL details, and Hibernate messages
  # must never be exposed to API consumers.
  # =========================================================================

  @NFR-SEC-03
  Scenario: [NFR-SEC-03] No information leakage on malformed JSON
    When the client sends a malformed JSON request to create a product
    Then the response status should be 400
    And the response body should contain a field "error"
    And the response body should not contain "exception"
    And the response body should not contain "stackTrace"
    And the response body should not contain "org.acme"
    And the response body should not contain "SQL"
    And the response body should not contain "Hibernate"

  @NFR-SEC-03
  Scenario: [NFR-SEC-03] No information leakage on unexpected server error
    When the client requests a product with SKU "FORCE-500"
    Then the response status should be 404
    And the response body should contain a field "error"
    And the response body should not contain "exception"
    And the response body should not contain "stackTrace"

  # =========================================================================
  # NFR-SEC-02 — Data Consistency (Optimistic Concurrency)
  # @Version on ProductEntity detects concurrent modifications.
  # =========================================================================

  @NFR-SEC-02
  Scenario: [NFR-SEC-02] Duplicate SKU is rejected with 409 Conflict
    Given a product exists with SKU "DUP-1" name "First TV" price 299.99 and stock 5
    When the client creates a product with:
      | sku   | name      | price  | stock |
      | DUP-1 | Second TV | 399.99 | 3     |
    Then the response status should be 409
    And the response body should contain a field "error"
    And the response body should not contain "exception"
    And the response body should not contain "SQL"

  # =========================================================================
  # NFR-RELY-01 — Latency Threshold
  # GET operations should complete within an acceptable threshold
  # under nominal single-node load.
  # =========================================================================

  @NFR-RELY-01
  Scenario: [NFR-RELY-01] Retrieve all products responds within latency threshold
    Given a product exists with SKU "LAT-1" name "Fast TV" price 199.00 and stock 10
    When the client retrieves all products
    Then the response status should be 200
    And the response time should be below 500 milliseconds

  @NFR-RELY-01
  Scenario: [NFR-RELY-01] Retrieve a product by SKU responds within latency threshold
    Given a product exists with SKU "LAT-2" name "Quick TV" price 249.00 and stock 8
    When the client retrieves the product with SKU "LAT-2"
    Then the response status should be 200
    And the response time should be below 500 milliseconds

  # =========================================================================
  # NFR-RELY-03 — Fault Tolerance
  # The catalog remains fully readable after a product is deleted.
  # Catalog integrity must hold after any mutation.
  # =========================================================================

  @NFR-RELY-03
  Scenario: [NFR-RELY-03] Catalog remains consistent after deletion
    Given a product exists with SKU "TV-KEEP" name "Keeper" price 99.00 and stock 1
    And a product exists with SKU "TV-DEL" name "ToDelete" price 49.00 and stock 1
    When the client deletes the product with SKU "TV-DEL"
    Then the response status should be 204
    When the client retrieves the product with SKU "TV-KEEP"
    Then the response status should be 200
    When the client retrieves the product with SKU "TV-DEL"
    Then the response status should be 404

  @NFR-RELY-03
  Scenario: [NFR-RELY-03] Listing remains stable after multiple mutations
    Given a product exists with SKU "STA-1" name "Stable A" price 10.00 and stock 1
    And a product exists with SKU "STA-2" name "Stable B" price 20.00 and stock 1
    And a product exists with SKU "STA-DEL" name "Transient" price 30.00 and stock 1
    When the client deletes the product with SKU "STA-DEL"
    Then the response status should be 204
    When the client retrieves all products
    Then the response status should be 200
    And the response should contain a product with SKU "STA-1"
    And the response should contain a product with SKU "STA-2"
    And the response should not contain a product with SKU "STA-DEL"
