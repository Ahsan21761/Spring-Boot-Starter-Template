package com.example.taskmanager.task.dto;

import com.example.taskmanager.task.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Create/update payload for a task. {@code status} is optional on create (defaults to TODO).
 */
public record TaskRequest(

        @Schema(example = "Write project README")
        @NotBlank(message = "title is required")
        @Size(max = 255, message = "title must be at most 255 characters")
        String title,

        @Schema(example = "Document setup, architecture, and API usage.")
        @Size(max = 5000, message = "description must be at most 5000 characters")
        String description,

        @Schema(example = "TODO")
        TaskStatus status,

        @Schema(example = "2026-08-01")
        LocalDate dueDate) {
}
