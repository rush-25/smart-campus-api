package com.smartcampus;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS Application configuration.
 * Sets the versioned API base path to /api/v1
 *
 * Default JAX-RS lifecycle: per-request (new instance per request).
 * Our DataStore is a singleton to persist in-memory data.
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    // JAX-RS scans com.smartcampus.* for @Path, @Provider annotations automatically
    // via ResourceConfig.packages() in Main.java
}
