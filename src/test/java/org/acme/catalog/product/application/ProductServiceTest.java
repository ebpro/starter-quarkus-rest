package org.acme.catalog.product.application;

import org.acme.catalog.product.application.ProductService;
import org.acme.catalog.product.application.dto.ProductDTO;
import org.acme.catalog.product.application.dto.usecases.CreateProductRequest;
import org.acme.catalog.product.infrastructure.persistence.ProductEntity;
import org.acme.catalog.product.infrastructure.persistence.ProductRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductService.
 * * Pedagogical points:
 * 1. Mocking the Persistence Layer: We test the Service in isolation.
 * 2. Testing Optionals: Handling empty vs present cases.
 * 3. Testing Business Rules: Verifying that standard Java exceptions are
 * thrown.
 */
@DisplayName("ProductService Unit Tests")
class ProductServiceTest {

    @Mock
    ProductRepository repository;

    ProductService service;

    @BeforeEach
    void setup() {
        // Initializes @Mock fields
        MockitoAnnotations.openMocks(this);
        service = new ProductService(repository);
    }

    @Test
    @DisplayName("getAll() should map all entities to DTOs")
    void getAll_returnsMappedDTOs() {
        // Arrange
        ProductEntity e = new ProductEntity("SKU1", "Chair", new BigDecimal("12.34"), 5);
        when(repository.listAll()).thenReturn(List.of(e));

        // Act
        var result = service.getAll();

        // Assert
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("SKU1", result.get(0).sku());
        verify(repository, times(1)).listAll();
    }

    @Test
    @DisplayName("getBySku() should return Optional with DTO when product exists")
    void getBySku_found() {
        ProductEntity e = new ProductEntity("SKU2", "Table", new BigDecimal("99.99"), 2);
        when(repository.findBySku("SKU2")).thenReturn(Optional.of(e));

        Optional<ProductDTO> result = service.getBySku("SKU2");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("SKU2", result.get().sku());
    }

    @Test
    @DisplayName("getBySku() should return empty Optional when product is missing")
    void getBySku_notFound() {
        when(repository.findBySku("MISSING")).thenReturn(Optional.empty());

        Optional<ProductDTO> result = service.getBySku("MISSING");

        Assertions.assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("create() should throw IllegalStateException on SKU conflict")
    void create_conflict_throwsException() {
        CreateProductRequest req = new CreateProductRequest("SKU3", "Lamp", new BigDecimal("5.00"), 10);
        // Simulate that the product already exists
        when(repository.findBySku("SKU3")).thenReturn(Optional.of(new ProductEntity()));

        // Act & Assert
        Assertions.assertThrows(IllegalStateException.class, () -> service.create(req));
        // Ensure persist was NEVER called
        verify(repository, never()).persist(any(ProductEntity.class));
    }

    @Test
    @DisplayName("create() should persist entity and return DTO on success")
    void create_success() {
        CreateProductRequest req = new CreateProductRequest("SKU4", "Sofa", new BigDecimal("199.99"), 1);
        when(repository.findBySku("SKU4")).thenReturn(Optional.empty());

        ProductDTO dto = service.create(req);

        Assertions.assertEquals("SKU4", dto.sku());
        // Verify that the repository's persist method was actually called
        verify(repository).persist(any(ProductEntity.class));
    }

    @Test
    @DisplayName("delete() should throw NoSuchElementException if SKU does not exist")
    void delete_notFound_throwsException() {
        when(repository.findBySku("NOT")).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class, () -> service.delete("NOT"));
    }

    @Test
    @DisplayName("delete() should call repository remove when product is found")
    void delete_found() {
        ProductEntity e = new ProductEntity("SKU5", "Desk", new BigDecimal("49.99"), 3);
        when(repository.findBySku("SKU5")).thenReturn(Optional.of(e));

        service.delete("SKU5");

        verify(repository).delete(e);
    }
}
