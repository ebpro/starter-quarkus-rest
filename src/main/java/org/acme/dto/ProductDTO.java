package org.acme.dto;

import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Data Transfer Object representing a product in the public API.
 *
 * <p>
 * Pedagogical points:
 * <ul>
 * <li>Read-only contract returned to API consumers.</li>
 * <li>Decoupled from persistence and domain implementation details.</li>
 * <li>Ensures API stability regardless of internal refactoring.</li>
 * </ul>
 */
@Schema(description = "Product representation for public API responses")
public record ProductDTO(

                @Schema(description = "Unique product identifier", examples = {
                                "SKU123" }) String sku,

                @Schema(description = "Product name", examples = { "Office Chair" }) String name,

                @Schema(description = "Product price", examples = { "50.00" }) BigDecimal price,

                @Schema(description = "Available stock quantity", examples = { "10" }) int stock) {
}
