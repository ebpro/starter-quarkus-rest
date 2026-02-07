package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

/**
 * Integration Tests for Technical Specifications (Non-Functional Requirements).
 * This class validates the observability stack [NFR-QA-03].
 */
@QuarkusTest
@Tag("NFR")
@DisplayName("Technical Specifications - Observability & Health")
class TechnicalSpecsIT {

    @Test
    @DisplayName("Liveness probe should be UP")
    void testHealthCheckLiveness() {
        given()
                .when().get("/q/health/live")
                .then()
                .statusCode(200)
                .header("Content-Type", containsString("application/json"))
                .body("status", is("UP"));
    }

    @Test
    @DisplayName("Readiness probe should be UP and all checks successful")
    void testHealthCheckReadiness() {
        given()
                .when().get("/q/health/ready")
                .then()
                .statusCode(200)
                .body("status", is("UP"))
                // Verify that no individual check is 'DOWN'
                .body("checks.status", not(hasItem("DOWN")));
    }

    @Test
    @DisplayName("Global Health UI should be accessible")
    void testHealthUI() {
        // Validates that the SmallRye Health UI is active
        given()
                .when().get("/q/health-ui/")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("OpenAPI Documentation should be available in YAML/JSON [NFR-ARCH-03]")
    void testOpenAPI() {
        given()
                .header("Accept", "application/json") // Explicitly ask for JSON
                .when().get("/q/openapi")
                .then()
                .statusCode(200)
                .contentType(containsString("json"))
                .body("openapi", startsWith("3.")); // Verifies it's a valid OpenAPI 3 document
    }
}
