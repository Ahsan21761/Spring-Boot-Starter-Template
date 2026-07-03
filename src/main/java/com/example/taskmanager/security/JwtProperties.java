package com.example.taskmanager.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Externalized JWT settings, bound from the {@code app.jwt.*} namespace and validated at startup.
 *
 * @param secret           Base64-encoded HMAC key; MUST be at least 256 bits (32 bytes) for HS256
 * @param expirationMinutes access-token lifetime in minutes
 * @param issuer           token issuer claim
 */
@Validated
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        @NotBlank String secret,
        @Positive long expirationMinutes,
        @NotBlank String issuer) {
}
