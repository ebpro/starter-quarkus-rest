package org.acme.api.cucumber;

import io.quarkiverse.cucumber.CucumberOptions;
import io.quarkiverse.cucumber.CucumberQuarkusTest;
import io.quarkus.test.common.QuarkusTestResource;
import org.acme.testresources.PostgreSqlTestResource;

@QuarkusTestResource(PostgreSqlTestResource.class)
@CucumberOptions(features = {
        "classpath:features/catalog"
}, tags = "@v1", glue = "org.acme.api.cucumber.steps")
public class CatalogAcceptanceIT extends CucumberQuarkusTest {
}
