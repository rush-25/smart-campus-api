package com.smartcampus.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Part 5 – Request &amp; Response Logging Filter
 *
 * Implements ContainerRequestFilter  → intercepts every incoming request
 *                                       BEFORE it reaches a resource method.
 * Implements ContainerResponseFilter → intercepts every outgoing response
 *                                       AFTER the resource method returns.
 *
 * @PreMatching ensures the request filter runs before URI-to-resource
 * matching, so even requests that result in 404 (no matching route) are
 * logged — a resource method would never see those.
 *
 * WHY A FILTER BEATS MANUAL LOGGING:
 *   • DRY      — one class covers every endpoint automatically.
 *   • Clean    — resource methods contain zero logging boilerplate.
 *   • Consistent — every log line follows the same format.
 *   • Togglable — remove the registration in ApplicationConfig and all
 *                 request/response logging disappears instantly.
 *   • AOP-style — cross-cutting concerns are handled in one place.
 */
@Provider
@PreMatching
public class LoggingFilter
        implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER =
            Logger.getLogger(LoggingFilter.class.getName());

    /**
     * Runs BEFORE the resource method.
     * Logs the HTTP verb and full request URI.
     */
    @Override
    public void filter(ContainerRequestContext requestCtx) throws IOException {
        LOGGER.info(String.format(
            "[REQUEST ] --> %-7s %s",
            requestCtx.getMethod(),
            requestCtx.getUriInfo().getRequestUri()
        ));
    }

    /**
     * Runs AFTER the resource method returns.
     * Logs the HTTP verb, URI, and response status code + reason phrase.
     */
    @Override
    public void filter(ContainerRequestContext  requestCtx,
                       ContainerResponseContext responseCtx) throws IOException {
        LOGGER.info(String.format(
            "[RESPONSE] <-- %-7s %s | %d %s",
            requestCtx.getMethod(),
            requestCtx.getUriInfo().getRequestUri(),
            responseCtx.getStatus(),
            responseCtx.getStatusInfo().getReasonPhrase()
        ));
    }
}
