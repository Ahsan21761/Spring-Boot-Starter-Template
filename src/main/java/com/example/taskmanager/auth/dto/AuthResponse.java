package com.example.taskmanager.auth.dto;

/**
 * Successful authentication result containing a bearer token and its metadata.
 *
 * @param accessToken   the signed JWT to send as {@code Authorization: Bearer <token>}
 * @param tokenType     always {@code Bearer}
 * @param expiresInMinutes token lifetime in minutes
 */
public record AuthResponse(String accessToken, String tokenType, long expiresInMinutes) {

    public static AuthResponse bearer(String accessToken, long expiresInMinutes) {
        return new AuthResponse(accessToken, "Bearer", expiresInMinutes);
    }
}
