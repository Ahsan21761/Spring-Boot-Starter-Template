package com.example.taskmanager.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Registration payload for creating a new account.
 */
public record RegisterRequest(

        @Schema(example = "jane.doe@example.com")
        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid address")
        String email,

        @Schema(example = "S3curePassw0rd!")
        @NotBlank(message = "password is required")
        @Size(min = 8, max = 72, message = "password must be between 8 and 72 characters")
        String password,

        @Schema(example = "Jane Doe")
        @Size(max = 255, message = "fullName must be at most 255 characters")
        String fullName) {
}
