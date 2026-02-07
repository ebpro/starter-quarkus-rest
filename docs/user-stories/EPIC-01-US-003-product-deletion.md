# US-003 – Product Deletion

## User Story

**As a** store manager
**I want to** delete a product from the catalog
**So that** it is no longer available for sale (e.g., end-of-life products)

## Acceptance Criteria

### 1. Successful Deletion

- **Given** the catalog contains a product with SKU "TV-1".
- **When** I delete the product with SKU "TV-1".
- **Then** the system should return a `204 No Content` status.
- **And** a subsequent search for "TV-1" should return a `404 Not Found`.

### 2. Delete Non-Existent Product

- **When** I attempt to delete a SKU that is not in the catalog.
- **Then** the system should return a `404 Not Found` error.

### 3. Catalog Integrity after Deletion

- **Given** two products "TV-1" and "LAP-1" exist.
- **When** I delete "TV-1".
- **Then** "LAP-1" should still be present and retrievable in the catalog.

## Technical Traceability

- **Automated Tests:** `src/test/resources/features/catalog/EPIC-01-US-003-product-deletion.feature`
