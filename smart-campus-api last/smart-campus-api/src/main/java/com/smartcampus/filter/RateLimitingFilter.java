package com.smartcampus.filter;

import com.smartcampus.response.ErrorResponse;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Rate Limiting Filter — EXTRA / BONUS FEATURE.
 *
 * Limits each client IP address to MAX_REQUESTS_PER_MINUTE requests per minute.
 * Exceeding this threshold returns HTTP 429 Too Many Requests.
 *
 * This protects the API from:
 *  - Denial-of-Service (DoS) attacks
 *  - Runaway client loops
 *  - Unintentional API abuse
 *
 * Uses a ConcurrentHashMap of AtomicIntegers for thread-safe, lock-free counting.
 * Counters reset every 60 seconds via a simple timestamp window check.
 */
@Provider
public class RateLimitingFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(RateLimitingFilter.class.getName());

    private static final int  MAX_REQUESTS_PER_MINUTE = 200;
    private static final long WINDOW_MS               = 60_000L;

    // Per-IP: [requestCount, windowStartMs]
    private final Map<String, long[]> ipCounters = new ConcurrentHashMap<>();

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String ip = getClientIp(requestContext);
        long   now = System.currentTimeMillis();

        long[] counter = ipCounters.computeIfAbsent(ip, k -> new long[]{0, now});

        synchronized (counter) {
            // Reset window if expired
            if (now - counter[1] > WINDOW_MS) {
                counter[0] = 0;
                counter[1] = now;
            }
            counter[0]++;

            if (counter[0] > MAX_REQUESTS_PER_MINUTE) {
                LOG.warning("Rate limit exceeded for IP: " + ip);
                ErrorResponse error = new ErrorResponse(
                        429,
                        "Too Many Requests",
                        "Rate limit of " + MAX_REQUESTS_PER_MINUTE +
                        " requests/minute exceeded. Please slow down."
                );
                requestContext.abortWith(
                        Response.status(429)
                                .type(MediaType.APPLICATION_JSON)
                                .entity(error)
                                .build()
                );
            }
        }
    }

    private String getClientIp(ContainerRequestContext ctx) {
        // Try X-Forwarded-For header first (proxy/load-balancer scenario)
        String forwarded = ctx.getHeaderString("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return "unknown";
    }
}
