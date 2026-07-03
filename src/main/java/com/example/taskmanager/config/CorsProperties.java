package com.example.taskmanager.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * CORS settings bound from {@code app.cors.*}. Defaults are permissive for local development;
 * tighten {@code allowed-origins} for production deployments.
 *
 * @param allowedOrigins origins permitted to call the API
 * @param allowedMethods HTTP methods permitted for cross-origin requests
 */
@ConfigurationProperties(prefix = "app.cors")
public record CorsProperties(
        List<String> allowedOrigins,
        List<String> allowedMethods) {

    public CorsProperties {
        if (allowedOrigins == null || allowedOrigins.isEmpty()) {
            allowedOrigins = List.of("http://localhost:3000");
        }
        if (allowedMethods == null || allowedMethods.isEmpty()) {
            allowedMethods = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
        }
    }
}
