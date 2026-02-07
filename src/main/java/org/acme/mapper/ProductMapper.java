package org.acme.mapper;

import org.acme.dto.ProductDTO;
import org.acme.persistence.ProductEntity;
import org.acme.dto.CreateProductRequest;
import org.acme.domain.Product;

/**
 * Component responsible for transforming data between architectural layers.
 * *
 * <p>
 * Pedagogical points:
 * <ul>
 * <li>Decouples the API from the persistence model.</li>
 * <li>Ensures the Domain model is the "source of truth" for business
 * rules.</li>
 * <li>Facilitates the transition from request payloads to database
 * records.</li>
 * </ul>
 */
public final class ProductMapper {

    private ProductMapper() {
        // Prevent instantiation of utility class
    }

    /**
     * Maps a Persistence Entity to an API Response DTO.
     * Used for outbound data (GET requests).
     *
     * @param entity the database record
     * @return the public data transfer object
     */
    public static ProductDTO toDto(ProductEntity entity) {
        return new ProductDTO(
                entity.getSku(),
                entity.getName(),
                entity.getPrice(),
                entity.getStock());
    }

    /**
     * Maps an API Request DTO to a Persistence Entity via the Domain model.
     * *
     * <p>
     * Process:
     * 1. Instantiate a Domain object to trigger business validation.
     * 2. If valid, map to a Persistence Entity for storage.
     *
     * @param request the validated creation payload
     * @return a valid product entity ready for persistence
     * @throws IllegalArgumentException if domain invariants are violated
     */
    public static ProductEntity toEntity(CreateProductRequest request) {
        // Step 1: Create Domain Object (Domain Rule Validation)
        // This triggers the business logic (e.g., price > 0).
        var domain = Product.of(
                request.sku(),
                request.name(),
                request.price(),
                request.stock());

        // Step 2: Convert to Persistence Entity
        // If execution reaches here, the data is guaranteed to be valid.
        return new ProductEntity(
                domain.sku(),
                domain.name(),
                domain.price(),
                domain.stock());
    }
}
