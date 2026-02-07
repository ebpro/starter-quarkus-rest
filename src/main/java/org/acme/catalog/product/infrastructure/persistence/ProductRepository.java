package org.acme.catalog.product.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Panache Repository for ProductEntity.
 *
 * <p>
 * Pedagogical points:
 * <ul>
 * <li>Implementation of the Repository Pattern to abstract data access.</li>
 * <li>Reduction of boilerplate code: Panache handles standard CRUD
 * operations.</li>
 * <li>Usage of the Active Record pattern's cousin (Repository style) for better
 * testability.</li>
 * </ul>
 */
@ApplicationScoped
public class ProductRepository implements PanacheRepository<ProductEntity> {

    // -------------------------------
    // Custom Queries (Panache Style)
    // -------------------------------

    /**
     * Retrieves a product by its unique business key (SKU).
     *
     * @param sku the product SKU
     * @return ProductEntity if found, otherwise null
     */
    public Optional<ProductEntity> findBySku(String sku) {
        // find() is a built-in Panache method
        return find("sku", sku).firstResultOptional();
    }

    /**
     * Returns all products currently available in inventory.
     *
     * @return a list of ProductEntity with stock > 0
     */
    public List<ProductEntity> findInStock() {
        return list("stock > 0");
    }

    /**
     * Finds products within a specific price range.
     *
     * @param min minimum price boundary
     * @param max maximum price boundary
     * @return a filtered list of ProductEntity
     */
    public List<ProductEntity> findByPriceRange(BigDecimal min, BigDecimal max) {
        // Usage of positional parameters (?1, ?2)
        return list("price >= ?1 and price <= ?2", min, max);
    }
}
