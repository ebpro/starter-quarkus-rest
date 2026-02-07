@v1
@EPIC-01
@US-001
Feature: Product creation
  In order to manage the product catalog
  As a store manager
  I want to create products with unique SKUs

  Background:
    Given the product catalog is empty

  Scenario: Create a product successfully
    Given a product with:
      | sku  | name      | price   | stock |
      | TV-1 | Smart TV  | 799.99  | 10    |
    When the client creates the product
    Then the response status should be 201
    And the created product should contain:
      | sku  | name      |
      | TV-1 | Smart TV  |

  Scenario: Reject creation when SKU already exists
    Given a product already exists with SKU "TV-1"
    When the client creates a product with:
      | sku  | name        | price  | stock |
      | TV-1 | Another TV  | 999.99 | 5     |
    Then the response status should be 409
    And the error message should contain "SKU already exists"

  Scenario: Created product should be retrievable
    Given a product with:
      | sku  | name      | price    | stock |
      | TV-2 | OLED TV   | 1299.99  | 4     |
    When the client creates the product
    And the client retrieves all products
    Then the product list should contain SKU "TV-2"
