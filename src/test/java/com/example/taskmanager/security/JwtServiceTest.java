package com.example.taskmanager.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pure unit tests for {@link JwtService} — no Spring context, no Docker.
 */
class JwtServiceTest {

    // Base64 of a 49-byte secret (>= 256 bits), sufficient for HS256.
    private static final String SECRET =
            "c3ByaW5nLWJvb3Qtc3RhcnRlci10ZW1wbGF0ZS1kZXYtc2VjcmV0LWNoYW5nZS1tZS1ub3c=";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(new JwtProperties(SECRET, 60, "test-issuer"));
    }

    @Test
    void generatesTokenWhoseSubjectRoundTrips() {
        String token = jwtService.generateToken("jane@example.com", Map.of("role", "USER"));

        assertThat(jwtService.isValid(token)).isTrue();
        assertThat(jwtService.extractSubject(token)).isEqualTo("jane@example.com");
    }

    @Test
    void rejectsTamperedToken() {
        String token = jwtService.generateToken("jane@example.com", Map.of());
        String tampered = token.substring(0, token.length() - 2) + "xx";

        assertThat(jwtService.isValid(tampered)).isFalse();
    }

    @Test
    void rejectsTokenSignedWithDifferentKey() {
        String otherSecret = "YW5vdGhlci1zZWNyZXQtdmFsdWUtdGhhdC1pcy1sb25nLWVub3VnaC1mb3ItaHMyNTY=";
        JwtService otherService = new JwtService(new JwtProperties(otherSecret, 60, "test-issuer"));
        String foreignToken = otherService.generateToken("attacker@example.com", Map.of());

        assertThat(jwtService.isValid(foreignToken)).isFalse();
    }

    @Test
    void rejectsGarbageInput() {
        assertThat(jwtService.isValid("not-a-jwt")).isFalse();
    }
}
