package com.example.taskmanager.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Credentials payload for authenticating an existing account.
 */
public record LoginRequest(

        @Schema(example = "jane.doe@example.com")
        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid address")
        String email,

        @Schema(example = "S3curePassw0rd!")
        @NotBlank(message = "password is required")
        String password) {
}
