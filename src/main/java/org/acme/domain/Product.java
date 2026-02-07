package org.acme.domain;

import java.math.BigDecimal;

/**
 * Immutable Domain Model representing a Product with enforced business
 * integrity.
 * *
 * <p>
 * Learning Objectives:
 * <ul>
 * <li>Utilizing Java Records for concise, immutable data carriers.</li>
 * <li>Implementing a <b>Compact Constructor</b> for unified validation.</li>
 * <li>Adopting the <b>Wither Pattern</b> for state transitions in immutable
 * objects.</li>
 * <li>Ensuring Domain Purity: No framework annotations (JPA/JSON) allowed
 * here.</li>
 * </ul>
 */
public record Product(String sku, String name, BigDecimal price, int stock) {

    /**
     * COMPACT CONSTRUCTOR
     * Absolute Security: Every instantiation (canonical or otherwise) must pass
     * this gate.
     * We throw {@link IllegalArgumentException}, which is caught by our
     * DomainExceptionMapper.
     */
    public Product {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU cannot be null or empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be null or negative");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        // Assignment is handled automatically by the Java Record runtime.
    }

    /**
     * Factory method for expressive object creation.
     */
    public static Product of(String sku, String name, BigDecimal price, int stock) {
        return new Product(sku, name, price, stock);
    }

    /**
     * WITHER: Returns a new instance with a modified Name.
     * Essential for maintaining immutability while updating state.
     */
    public Product withName(String newName) {
        return new Product(this.sku, newName, this.price, this.stock);
    }

    /**
     * WITHER: Returns a new instance with a modified Price.
     */
    public Product withPrice(BigDecimal newPrice) {
        return new Product(this.sku, this.name, newPrice, this.stock);
    }

    /**
     * WITHER: Returns a new instance with a modified Stock level.
     */
    public Product withStock(int newStock) {
        return new Product(this.sku, this.name, this.price, newStock);
    }

    /**
     * Domain Logic: Encapsulates business questions inside the data model.
     */
    public boolean isInStock() {
        return stock > 0;
    }

    @Override
    public String toString() {
        return String.format("Product[sku=%s, name=%s, price=%s, stock=%d]",
                sku, name, price, stock);
    }
}
