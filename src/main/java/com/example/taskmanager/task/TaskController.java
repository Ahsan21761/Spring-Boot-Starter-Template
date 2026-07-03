package com.example.taskmanager.task;

import com.example.taskmanager.common.dto.PageResponse;
import com.example.taskmanager.security.AppUserDetails;
import com.example.taskmanager.task.dto.TaskRequest;
import com.example.taskmanager.task.dto.TaskResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * CRUD endpoints for the authenticated user's tasks. All routes require a valid JWT; the
 * owner is derived from the token, never from client input.
 */
@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks", description = "Manage the authenticated user's tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "List the current user's tasks (paged, optionally filtered by status)")
    public PageResponse<TaskResponse> list(
            @AuthenticationPrincipal AppUserDetails principal,
            @RequestParam(required = false) TaskStatus status,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return PageResponse.from(taskService.list(principal.getId(), status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Fetch a single task by id")
    public TaskResponse get(
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable Long id) {
        return taskService.get(principal.getId(), id);
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskResponse> create(
            @AuthenticationPrincipal AppUserDetails principal,
            @Valid @RequestBody TaskRequest request,
            UriComponentsBuilder uriBuilder) {
        TaskResponse created = taskService.create(principal.getId(), request);
        var location = uriBuilder.path("/api/v1/tasks/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing task")
    public TaskResponse update(
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        return taskService.update(principal.getId(), id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Deleted")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable Long id) {
        taskService.delete(principal.getId(), id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
