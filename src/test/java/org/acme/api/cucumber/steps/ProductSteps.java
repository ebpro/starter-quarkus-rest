package org.acme.api.cucumber.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import org.acme.api.client.ProductRestClient;
import org.acme.api.cucumber.VersionContext;
import org.acme.dto.CreateProductRequest;
import org.acme.dto.ProductDTO;
import org.acme.persistence.ProductRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ProductSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductSteps.class);

    @Inject
    ProductRepository repository;

    @Inject
    @RestClient
    ProductRestClient apiClient;

    @Inject
    VersionContext versionContext;

    private CreateProductRequest pendingRequest;

    private String v() {
        return versionContext.getVersion();
    }

    private Response lastResponse() {
        return versionContext.getLastResponse();
    }

    private void setResponse(Response response) {
        if (response != null) {
            response.bufferEntity();
        }
        versionContext.setLastResponse(response);
    }

    // -------------------- GIVEN --------------------

    @Transactional
    @Given("the product catalog is empty")
    public void theProductCatalogIsEmpty() {
        // 1. Supprime tout
        repository.deleteAll();

        // 2. Force l'écriture immédiate dans Postgres
        repository.flush();

        // 3. TRÈS IMPORTANT : Vide le cache de la session Hibernate actuelle.
        // Cela force Hibernate à refaire un SELECT réel en base lors du prochain appel.
        repository.getEntityManager().clear();

        LOGGER.info("Product catalog cleared, flushed and session cleared.");
    }

    @Given("a product with:")
    public void aProductWith(DataTable table) {
        this.pendingRequest = mapToCreateRequest(table.asMaps().get(0));
    }

    @Given("a product already exists with SKU {string}")
    public void aProductAlreadyExistsWithSKU(String sku) {
        CreateProductRequest req = new CreateProductRequest(sku, "Existing Product", new BigDecimal("123.45"), 1);
        Response r = apiClient.createRaw(v(), req).await().indefinitely();

        if (r.getStatus() != 201 && r.getStatus() != 409) {
            throw new RuntimeException("Setup failed: " + r.getStatus());
        }
    }

    @Given("the product catalog contains only the following products:")
    public void theProductCatalogContains(DataTable table) {
        theProductCatalogIsEmpty();
        table.asMaps().forEach(row -> {
            Response r = apiClient.createRaw(v(), mapToCreateRequest(row)).await().indefinitely();
            if (r.getStatus() >= 400 && r.getStatus() != 409) {
                throw new RuntimeException("Failed to seed product: " + r.readEntity(String.class));
            }
        });
    }

    // -------------------- WHEN --------------------

    @When("the client creates a product with:")
    public void theClientCreatesAProductWith(DataTable table) {
        this.pendingRequest = mapToCreateRequest(table.asMaps().get(0));
        setResponse(apiClient.createRaw(v(), pendingRequest).await().indefinitely());
    }

    @When("the client creates the product")
    public void theClientCreatesTheProduct() {
        if (pendingRequest == null)
            throw new IllegalStateException("No product defined");
        setResponse(apiClient.createRaw(v(), pendingRequest).await().indefinitely());
        LOGGER.info("Created product. Status: {}", lastResponse().getStatus());
    }

    @When("the client retrieves all products")
    public void theClientRetrievesAllProducts() {
        setResponse(apiClient.getAllRaw(v()).await().indefinitely());
    }

    @When("the client retrieves the product with SKU {string}")
    public void theClientRetrievesProductBySKU(String sku) {
        setResponse(apiClient.getBySkuRaw(v(), sku).await().indefinitely());
    }

    @When("the client deletes the product with SKU {string}")
    public void theClientDeletesProductBySKU(String sku) {
        setResponse(apiClient.deleteRaw(v(), sku).await().indefinitely());
    }

    // -------------------- THEN --------------------

    @Then("the created product should contain:")
    public void theCreatedProductShouldContain(DataTable table) {
        theReturnedProductShouldContain(table);
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int status) {
        assertThat("HTTP Status Mismatch", lastResponse().getStatus(), is(status));
    }

    @Then("the returned product should contain:")
    public void theReturnedProductShouldContain(DataTable table) {
        ProductDTO actual = lastResponse().readEntity(ProductDTO.class);
        Map<String, String> expected = table.asMaps().get(0);

        if (expected.containsKey("sku")) {
            assertThat(actual.sku(), equalTo(expected.get("sku")));
        }

        if (expected.containsKey("name")) {
            assertThat(actual.name(), equalTo(expected.get("name")));
        }

        if (expected.containsKey("price") && expected.get("price") != null) {
            assertThat(actual.price().compareTo(new BigDecimal(expected.get("price"))), is(0));
        }
    }

    @Then("the error message should contain {string}")
    public void theErrorMessageShouldContain(String msg) {
        String body = lastResponse().readEntity(String.class);
        assertThat(body, containsString(msg));
    }

    @Then("the product list should contain SKU {string}")
    public void theProductListShouldContainSKU(String sku) {
        List<ProductDTO> list = lastResponse().readEntity(new GenericType<List<ProductDTO>>() {
        });
        boolean found = list.stream().anyMatch(p -> p.sku().equals(sku));
        assertThat("SKU " + sku + " not found in list", found, is(true));
    }

    @Then("the product list should contain SKUs {string}")
    public void theProductListShouldContainSKUs(String skus) {
        List<String> expectedSkus = List.of(skus.split(",\\s*"));

        List<ProductDTO> actualProducts = lastResponse().readEntity(new GenericType<List<ProductDTO>>() {
        });
        List<String> actualSkus = actualProducts.stream().map(ProductDTO::sku).toList();

        assertThat(actualSkus, containsInAnyOrder(expectedSkus.toArray()));
    }

    @Then("the product with SKU {string} should no longer exist in the catalog")
    public void theProductWithSKUShouldNoLongerExist(String sku) {
        Response r = apiClient.getBySkuRaw(v(), sku).await().indefinitely();
        assertThat(r.getStatus(), is(404));
    }

    @Then("the response should contain {int} product(s)")
    public void theResponseShouldContainNProducts(int n) {
        // Utilisation de GenericType pour rester cohérent avec ton client
        // RestClient/Jackson
        List<ProductDTO> list = lastResponse().readEntity(new GenericType<List<ProductDTO>>() {
        });

        assertThat("Number of products in response mismatch", list, hasSize(n));
        LOGGER.info("Verified response contains {} product(s)", n);
    }

    // -------------------- PRIVATE HELPERS --------------------

    private CreateProductRequest mapToCreateRequest(Map<String, String> row) {
        return new CreateProductRequest(
                row.get("sku"),
                row.get("name"),
                new BigDecimal(row.get("price")),
                Integer.parseInt(row.get("stock")));
    }

    // -------------------- NEW NFR WHEN --------------------

    @When("the client sends a malformed JSON request")
    public void theClientSendsMalformedJson() {
        // Broken JSON with missing value for "stock"
        // Note: The endpoint expects a JSON body, but we are sending an invalid one to
        // test error handling and information leakage.
        String brokenJson = """
                {
                  "sku": "BAD-JSON",
                  "price": 100.0,
                  "stock":
                }
                """;
        setResponse(apiClient.postRawBody(v(), brokenJson).await().indefinitely());
    }

    // -------------------- NEW NFR THEN --------------------

    @Then("the response should not leak internal server details")
    public void theResponseShouldNotLeakInternalServerDetails() {
        String body = lastResponse().readEntity(String.class);

        // Check for common stack trace indicators or exception names that should not be
        // exposed in a sanitized error message. [NFR-SEC-03]
        assertThat("Security Leak: Stacktrace found", body, not(containsString("at io.quarkus")));
        assertThat("Security Leak: Database info found", body, not(containsString("hibernate")));
        assertThat("Security Leak: Internal Exception name found", body, not(containsString("Exception")));

        LOGGER.info("Security check passed: No technical leakage detected in response.");
    }

}
