package org.acme.api.client.filters;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * A client-side filter used exclusively during integration testing to improve
 * observability.
 * *
 * <p>
 * Learning Objectives:
 * <ul>
 * <li>Understanding the JAX-RS Client Filter lifecycle.</li>
 * <li>Handling non-repeatable InputStreams (read and re-inject).</li>
 * <li>Automated debugging of API failures (HTTP 400+) during BDD/Cucumber
 * execution.</li>
 * </ul>
 */
public class TestDebugFilter implements ClientResponseFilter {
    private static final Logger LOG = LoggerFactory.getLogger(TestDebugFilter.class);

    /**
     * Intercepts the response from the server before it reaches the test
     * assertions.
     * * @param requestContext Context of the outgoing request (Method, URI).
     * 
     * @param responseContext Context of the incoming response (Status, Entity).
     * @throws IOException If stream manipulation fails.
     */
    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        int status = responseContext.getStatus();

        // Step 1: Only intercept errors (>= 400) to keep logs concise.
        if (status >= 400 && responseContext.hasEntity()) {
            InputStream stream = responseContext.getEntityStream();
            if (stream != null) {
                // Step 2: Read the bytes from the stream.
                byte[] entityBytes = stream.readAllBytes();
                String body = new String(entityBytes, StandardCharsets.UTF_8);

                // Step 3: Log the failure details for the student to debug.
                LOG.error("--- API FAILURE DEBUG ---");
                LOG.error("Method: {}", requestContext.getMethod());
                LOG.error("URI:    {}", requestContext.getUri());
                LOG.error("Status: {}", status);
                LOG.error("Body:   {}", body.isEmpty() ? "<empty>" : body);
                LOG.error("-------------------------");

                /*
                 * Step 4: CRITICAL PEDAGOGICAL POINT.
                 * InputStreams are ephemeral. Once readAllBytes() is called, the stream is
                 * exhausted.
                 * We must re-inject a fresh stream so the test code (Cucumber/RestAssured)
                 * can still perform assertions on the response body.
                 */
                responseContext.setEntityStream(new ByteArrayInputStream(entityBytes));
            }
        }
    }
}
