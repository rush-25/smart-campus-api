package com.smartcampus.config;

import com.smartcampus.filter.LoggingFilter;
import com.smartcampus.mapper.GlobalExceptionMapper;
import com.smartcampus.mapper.LinkedResourceNotFoundExceptionMapper;
import com.smartcampus.mapper.ResourceNotFoundExceptionMapper;
import com.smartcampus.mapper.RoomNotEmptyExceptionMapper;
import com.smartcampus.mapper.SensorUnavailableExceptionMapper;
import com.smartcampus.resource.DiscoveryResource;
import com.smartcampus.resource.RoomResource;
import com.smartcampus.resource.SensorResource;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * Central JAX-RS application configuration.
 *
 * @ApplicationPath("/api/v1") defines the base URI prefix for all endpoints.
 * All resources, filters, and exception mappers are explicitly registered here
 * for full visibility and control.
 */
@ApplicationPath("/api/v1")
public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig() {

        // ── Resources ─────────────────────────────────────────────
        register(DiscoveryResource.class);
        register(RoomResource.class);
        register(SensorResource.class);

        // ── JSON Support ───────────────────────────────────────────
        register(JacksonFeature.class);

        // ── Filters ────────────────────────────────────────────────
        register(LoggingFilter.class);

        // ── Exception Mappers ──────────────────────────────────────
        register(RoomNotEmptyExceptionMapper.class);
        register(LinkedResourceNotFoundExceptionMapper.class);
        register(SensorUnavailableExceptionMapper.class);
        register(ResourceNotFoundExceptionMapper.class);
        register(GlobalExceptionMapper.class);   // catch-all — register last
    }
}
