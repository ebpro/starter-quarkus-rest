@v1
@EPIC-01
@US-002
Feature: Product retrieval
  In order to view the products in the catalog
  As a store manager or customer
  I want to retrieve products by SKU or list all products

  Background:
    Given the product catalog contains only the following products:
      | sku   | name       | price   | stock |
      | TV-1  | Smart TV   | 799.99  | 10    |
      | LAP-1 | Laptop     | 1299.99 | 5     |

  Scenario: Retrieve all products
    When the client retrieves all products
    Then the response status should be 200
    And the response should contain 2 products
    And the product list should contain SKUs "TV-1,LAP-1"

  Scenario: Retrieve a product by SKU successfully
    When the client retrieves the product with SKU "TV-1"
    Then the response status should be 200
    And the returned product should contain:
      | sku  | name      |
      | TV-1 | Smart TV  |

  Scenario: Fail to retrieve a non-existent product
    When the client retrieves the product with SKU "NON-EXISTENT"
    Then the response status should be 404
    And the error message should contain "Product not found"
