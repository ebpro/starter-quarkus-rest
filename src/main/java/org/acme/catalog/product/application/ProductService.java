package org.acme.catalog.product.application;

import org.acme.catalog.product.application.dto.ProductDTO;
import org.acme.catalog.product.application.dto.usecases.CreateProductRequest;
import org.acme.catalog.product.infrastructure.mapper.ProductMapper;
import org.acme.catalog.product.infrastructure.persistence.ProductEntity;
import org.acme.catalog.product.infrastructure.persistence.ProductRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Business Service demonstrating Clean Architecture and Productivity.
 *
 * <p>
 * Learning Objectives:
 * <ul>
 * <li>Decoupling Business Logic from the Web Framework (no JAX-RS
 * dependencies).</li>
 * <li>Leveraging Quarkus Panache for high-productivity data access.</li>
 * <li>Using {@code Optional} as a return type to delegate missing-value policy
 * to the caller.</li>
 * <li>Adopting Java 16+ features (toList) for cleaner functional
 * pipelines.</li>
 * </ul>
 */
@ApplicationScoped
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves all products.
     * Uses Java 16+ .toList() to simplify the stream collector.
     */
    public List<ProductDTO> getAll() {
        return repository.listAll().stream()
                .map(ProductMapper::toDto)
                .toList();
    }

    /**
     * Returns an Optional DTO.
     * *
     * <p>
     * Pedagogical Point: the Service doesn't throw a Web Exception.
     * It returns an Optional, allowing the Resource to decide the appropriate
     * response (e.g., 404 Not Found vs. a 204 No Content).
     */
    public Optional<ProductDTO> getBySku(String sku) {
        return repository.findBySku(sku)
                .map(ProductMapper::toDto);
    }

    /**
     * Creates a product using business domain logic.
     * * @throws IllegalStateException (Standard Java) for business rule violations.
     */
    @Transactional
    public ProductDTO create(CreateProductRequest request) {
        // Step 1: Mapping (Validation happens inside Mapper/Domain Record)
        ProductEntity entity = ProductMapper.toEntity(request);

        // Step 2: Integrity Rule (Using Standard Java Exceptions)
        if (repository.findBySku(entity.getSku()).isPresent()) {
            throw new IllegalStateException("Product SKU already exists: " + entity.getSku());
        }

        // Step 3: Persistence via Panache
        repository.persist(entity);

        return ProductMapper.toDto(entity);
    }

    /**
     * Deletes a product. Throws standard Java NoSuchElementException if missing.
     */
    @Transactional
    public void delete(String sku) {
        ProductEntity p = repository.findBySku(sku)
                .orElseThrow(() -> new NoSuchElementException("Product not found: " + sku));
        repository.delete(p);
    }

    /**
     * WARNING: This method is intended for testing purposes only. It performs a
     * bulk delete of all products without any safety checks or pagination, which
     * can lead to performance issues and unintended data loss in a production
     * environment. Use with caution.
     */
    @Transactional
    public void clearAll() {
        repository.deleteAll();
    }
}
