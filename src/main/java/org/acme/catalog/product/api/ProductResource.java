package org.acme.catalog.product.api;

import org.acme.catalog.product.application.ProductService;
import org.acme.catalog.product.application.dto.ProductDTO;
import org.acme.catalog.product.application.dto.usecases.CreateProductRequest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * REST resource: Production-ready API with validation and documentation.
 *
 * <p>
 * Learning Objectives:
 * <ul>
 * <li>Integrating Bean Validation (@Valid) to protect the API entry point.</li>
 * <li>Leveraging MicroProfile OpenAPI for standardized documentation.</li>
 * <li>Refining the Java-to-HTTP contract (Exception mapping
 * documentation).</li>
 * </ul>
 */
@Path("/api/v1/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Product", description = "Validated and documented production-ready API")
@RequestScoped
public class ProductResource {

    private final ProductService service;

    @Inject
    public ProductResource(ProductService service) {
        this.service = service;
    }

    /**
     * @return a list of all products mapped to DTOs.
     */
    @GET
    @Operation(summary = "Get all products", description = "Returns a list of all products in the catalog")
    @APIResponse(responseCode = "200", description = "List of products", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class, type = SchemaType.ARRAY)))
    public List<ProductDTO> getAll() {
        return service.getAll();
    }

    /**
     * @param sku the unique Stock Keeping Unit to search for.
     * @return the matching product DTO.
     * @throws NoSuchElementException if the SKU does not exist (mapped to 404).
     */
    @GET
    @Path("/{sku}")
    @Operation(summary = "Get product by SKU", description = "Finds a specific product using its unique SKU")
    @APIResponse(responseCode = "200", description = "Product found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class)))
    @APIResponse(responseCode = "404", description = "Product not found")
    public ProductDTO getBySku(
            @Parameter(description = "The SKU of the product", required = true, examples = @ExampleObject(value = "SKU123")) @PathParam("sku") String sku) {
        return service.getBySku(sku)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));
    }

    /**
     * @param request validated product creation payload.
     * @return a 201 Created response with the created product.
     * @throws IllegalArgumentException if domain rules are violated (mapped to
     *                                  400).
     * @throws IllegalStateException    if the SKU is already in use (mapped to
     *                                  409).
     */
    @POST
    @Operation(summary = "Create a new product", description = "Validates input and persists a new product")
    @APIResponse(responseCode = "201", description = "Product created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class)))
    @APIResponse(responseCode = "400", description = "Invalid input or domain validation failure")
    @APIResponse(responseCode = "409", description = "SKU already exists")
    public Response create(@Valid CreateProductRequest request) {
        ProductDTO created = service.create(request);
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }

    /**
     * @param sku the unique SKU of the product to remove.
     * @return a 204 No Content response on success.
     * @throws NoSuchElementException if the product is not found (mapped to 404).
     */
    @DELETE
    @Path("/{sku}")
    @Operation(summary = "Delete a product", description = "Removes a product from the database by its SKU")
    @APIResponse(responseCode = "204", description = "Product deleted")
    @APIResponse(responseCode = "404", description = "Product not found")
    public Response delete(
            @Parameter(description = "The SKU of the product to delete", required = true) @PathParam("sku") String sku) {
        service.delete(sku);
        return Response.noContent().build();
    }
}
