# US-002 – Product Retrieval

## User Story

**As a** store manager
**I want to** retrieve one or all products from the catalog
**So that** I can check their current prices and stock levels

## Acceptance Criteria

### 1. Retrieve All Products

- **Given** the catalog contains several products.
- **When** I request the list of all products.
- **Then** I should receive a list containing all existing SKUs with a `200 OK` status.

### 2. Retrieve a Specific Product by SKU

- **Given** a product with SKU "TV-1" exists.
- **When** I request the product with SKU "TV-1".
- **Then** the system should return the full details (name, price, stock) with a `200 OK` status.

### 3. Handle Non-Existent Product

- **When** I request a SKU that does not exist in the system.
- **Then** the system should return a `404 Not Found` error with the message "Product not found".

## Technical Traceability

- **Automated Tests:** `src/test/resources/features/catalog/EPIC-01-US-002-product-retrieval.feature`
