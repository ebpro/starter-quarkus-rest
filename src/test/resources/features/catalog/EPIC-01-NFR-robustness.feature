@v1
@NFR
@Security
Feature: Catalog Robustness and Security
  In order to ensure system stability and data integrity
  As a store manager
  I want the system to reject invalid data and protect internal information

  Background:
    Given the product catalog is empty

  Scenario: [NFR-SEC-01] Reject invalid prices
    When the client creates a product with:
      | sku   | name      | price  | stock |
      | BAD-P | Cheap TV  | -10.00 | 5     |
    Then the response status should be 400

  Scenario: [NFR-SEC-01] Reject invalid stock levels
    When the client creates a product with:
      | sku   | name       | price  | stock |
      | STK-1 | Negative   | 10.00  | -5    |
    Then the response status should be 400

  Scenario: [NFR-SEC-03] No information leakage on malformed request
    When the client sends a malformed JSON request
    Then the response status should be 400
    And the response should not leak internal server details
