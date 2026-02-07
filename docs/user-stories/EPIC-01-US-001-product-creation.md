# US-001 – Product Creation

## User Story

**As a** store manager
**I want to** create a product
**So that** it can be sold in the catalog

## Acceptance Criteria

### 1. Successful Product Creation

- **Given** the product catalog is empty.
- **When** I provide valid product details (SKU, name, price, stock).
- **Then** the system should create the product and return a `201 Created` status.
- **And** the response should contain the details of the newly created product.

### 2. Reject Duplicate SKU

- **Given** a product already exists with SKU "TV-1".
- **When** I attempt to create another product with the same SKU "TV-1".
- **Then** the system should return a `409 Conflict` error.
- **And** the error message should indicate that the SKU already exists.

### 3. Verify Persistence

- **Given** I have successfully created a product with SKU "TV-2".
- **When** I retrieve all products from the catalog.
- **Then** the product list should contain the product with SKU "TV-2".

## Technical Traceability

- **Automated Tests:** `src/test/resources/features/catalog/EPIC-01-US-001-product-creation.feature`
