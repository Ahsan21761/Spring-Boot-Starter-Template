package com.example.taskmanager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Issues and validates stateless HS256 JSON Web Tokens. The signing key is derived from a
 * Base64-encoded secret supplied via configuration.
 */
@Slf4j
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMinutes;
    private final String issuer;

    public JwtService(JwtProperties properties) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()));
        this.expirationMinutes = properties.expirationMinutes();
        this.issuer = properties.issuer();
    }

    /**
     * Generates a signed token for the given subject (the user's email).
     *
     * @param subject the authenticated principal identifier
     * @param claims  additional claims to embed (e.g. role)
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant expiry = now.plus(expirationMinutes, ChronoUnit.MINUTES);
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey)
                .compact();
    }

    /** Returns the subject (email) if the token is valid, otherwise empty via exception. */
    public String extractSubject(String token) {
        return parse(token).getSubject();
    }

    /** True if the token's signature and expiry are valid. Never throws. */
    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Rejected JWT: {}", ex.getMessage());
            return false;
        }
    }

    public long getExpirationMinutes() {
        return expirationMinutes;
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
