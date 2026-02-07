package org.acme.api.client;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.api.client.filters.TestDebugFilter;
import org.acme.dto.CreateProductRequest;
import org.acme.dto.ProductDTO;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

/**
 * Type-safe REST Client for the Product API based on MicroProfile Rest Client.
 * *
 * <p>
 * Learning Objectives:
 * <ul>
 * <li>Declarative API definition using JAX-RS annotations.</li>
 * <li>Handling multiple API versions via {@code @PathParam}.</li>
 * <li>Using {@link RegisterProvider} to inject custom interceptors
 * (filters).</li>
 * <li>Differentiating between Domain-mapped returns and Raw HTTP
 * responses.</li>
 * </ul>
 */
@RegisterRestClient(configKey = "product-api")
@Path("/api/{version}/products")
@RegisterProvider(TestDebugFilter.class) // Attach the debugger flight recorder
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ProductRestClient {

    /**
     * TYPED: Returns a deserialized list of products.
     * Best used when you expect success (e.g., in a Dashboard UI).
     */
    @GET
    Uni<List<ProductDTO>> getAll(@PathParam("version") String version);

    /**
     * TYPED: Returns a specific product DTO.
     * Note: This will automatically throw an exception if the server returns 404.
     */
    @GET
    @Path("/{sku}")
    Uni<ProductDTO> getBySku(@PathParam("version") String version, @PathParam("sku") String sku);

    // --- RAW METHODS (FOR BDD TESTING) ---

    /**
     * RAW: Returns the full HTTP Response.
     * Allows Cucumber steps to assert: {@code then status code is 200}.
     */
    @GET
    Uni<Response> getAllRaw(@PathParam("version") String version);

    @GET
    @Path("/{sku}")
    Uni<Response> getBySkuRaw(@PathParam("version") String version, @PathParam("sku") String sku);

    /**
     * RAW: Attempts product creation.
     * Used to test both success (201) and business conflicts (409).
     */
    @POST
    Uni<Response> createRaw(@PathParam("version") String version, CreateProductRequest request);

    @DELETE
    @Path("/{sku}")
    Uni<Response> deleteRaw(@PathParam("version") String version, @PathParam("sku") String sku);

    /**
     * LOW-LEVEL: Sends a raw String as JSON.
     * Essential for testing robustness against malformed JSON (NFR-SEC-03).
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Uni<Response> postRawBody(@PathParam("version") String version, String body);
}
