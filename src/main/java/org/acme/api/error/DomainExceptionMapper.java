package org.acme.api.error;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps {@link IllegalArgumentException} to an HTTP 400 Bad Request.
 * * Learning Objective: Handle domain validation failures.
 * If a Product Record or DTO fails internal validation, this mapper ensures
 * the client knows exactly what was wrong with the input.
 */
@Provider
public class DomainExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
    @Override
    public Response toResponse(IllegalArgumentException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(exception.getMessage())
                .build();
    }
}
