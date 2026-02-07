package org.acme.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * DTO used for product creation requests.
 *
 * <p>Pedagogical points:
 * <ul>
 * <li>Defines the write-only contract for the API.</li>
 * <li>Includes Bean Validation constraints to enforce data integrity.</li>
 * <li>Implemented as an immutable Java record.</li>
 * </ul>
 */
@Schema(description = "Request object to create a new product")
public record CreateProductRequest(

        @Schema(description = "Unique SKU of the product", examples = { "SKU123" })
        @NotBlank(message = "SKU is required")
        String sku,

        @Schema(description = "Name of the product", examples = { "Office Chair" })
        @NotBlank(message = "Name is required")
        String name,

        @Schema(description = "Unit price", examples = { "50.00" })
        @NotNull(message = "Price is required")
        @PositiveOrZero(message = "Price must be positive or zero")
        BigDecimal price,

        @Schema(description = "Initial stock quantity", examples = { "10" })
        @PositiveOrZero(message = "Stock cannot be negative")
        int stock
) {}
