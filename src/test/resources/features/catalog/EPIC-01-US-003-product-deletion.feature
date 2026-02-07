@v1
@EPIC-01
@US-003
Feature: Product deletion
  In order to remove outdated or incorrect products
  As a store manager
  I want to delete products by SKU

  Background:
    Given the product catalog contains only the following products:
      | sku   | name       | price   | stock |
      | TV-1  | Smart TV   | 799.99  | 10    |
      | LAP-1 | Laptop     | 1299.99 | 5     |

  Scenario: Delete an existing product successfully
    When the client deletes the product with SKU "TV-1"
    Then the response status should be 204
    And the product with SKU "TV-1" should no longer exist in the catalog

  Scenario: Attempt to delete a non-existent product
    When the client deletes the product with SKU "NON-EXISTENT"
    Then the response status should be 404
    And the error message should contain "Product not found"

  Scenario: Delete an existing product and verify remaining products
    When the client deletes the product with SKU "TV-1"
    Then the response status should be 204
    When the client retrieves all products
    Then the response status should be 200
    And the response should contain 1 product
    And the product list should contain SKU "LAP-1"
