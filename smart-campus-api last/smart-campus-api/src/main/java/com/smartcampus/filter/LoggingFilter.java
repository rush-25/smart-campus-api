package com.smartcampus.filter;

import com.smartcampus.storage.DataStore;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * API Observability Filter — logs every inbound request and outbound response.
 *
 * Implements BOTH ContainerRequestFilter and ContainerResponseFilter so that
 * a single class handles the complete request lifecycle.
 *
 * Using a JAX-RS filter for cross-cutting concerns (logging, auth, CORS) is
 * superior to adding Logger.info() inside every resource method because:
 *  1. Single Responsibility — resource methods focus only on business logic.
 *  2. DRY — one location to maintain logging format.
 *  3. Guaranteed execution — runs even for requests that hit exception mappers.
 *  4. Easily toggled — can be enabled/disabled without touching resource code.
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger("SmartCampus.API");

    /** Called BEFORE the resource method executes. */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        DataStore.getInstance().incrementRequests();
        String method = requestContext.getMethod();
        String uri    = requestContext.getUriInfo().getRequestUri().toString();
        LOG.info(String.format("[REQUEST]  %s %s", method, uri));
    }

    /** Called AFTER the resource method (and any exception mapper) returns. */
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        int    status = responseContext.getStatus();
        String method = requestContext.getMethod();
        String uri    = requestContext.getUriInfo().getRequestUri().toString();
        LOG.info(String.format("[RESPONSE] %s %s → HTTP %d", method, uri, status));
    }
}
