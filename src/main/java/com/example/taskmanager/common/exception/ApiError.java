package com.example.taskmanager.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

/**
 * Standard, RFC-7807-inspired error payload returned for every non-2xx response.
 *
 * @param timestamp   when the error occurred (UTC)
 * @param status      HTTP status code
 * @param error       HTTP status reason phrase
 * @param message     human-readable, safe-to-expose description
 * @param path        request URI that produced the error
 * @param fieldErrors per-field validation failures (only present for 400 validation errors)
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldError> fieldErrors) {

    public record FieldError(String field, String message) {}

    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(Instant.now(), status, error, message, path, List.of());
    }
}
