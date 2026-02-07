package org.acme.api.cucumber;

import io.quarkiverse.cucumber.ScenarioScope;
import jakarta.ws.rs.core.Response;

/**
 * A simple CDI bean to hold the current API version for the tests.
 * This allows us to switch between versions in a more flexible way.
 */

@ScenarioScope
public class VersionContext {
    private String version;
    private Response lastResponse;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Response getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(Response lastResponse) {
        this.lastResponse = lastResponse;
    }
}
