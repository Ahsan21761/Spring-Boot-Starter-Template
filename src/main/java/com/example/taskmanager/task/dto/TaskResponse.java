package com.example.taskmanager.task.dto;

import com.example.taskmanager.task.TaskStatus;
import java.time.Instant;
import java.time.LocalDate;

/**
 * API representation of a task. Decoupled from the entity so persistence changes never leak
 * into the public contract.
 */
public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        LocalDate dueDate,
        Long ownerId,
        Instant createdAt,
        Instant updatedAt) {
}
