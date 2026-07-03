package com.example.taskmanager.task;

import com.example.taskmanager.task.dto.TaskRequest;
import com.example.taskmanager.task.dto.TaskResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Compile-time, null-safe mapping between {@link Task} entities and their DTOs. Managed fields
 * (id, owner, audit columns) are never overwritten from client input.
 */
@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "ownerId", source = "owner.id")
    TaskResponse toResponse(Task task);

    @Mapping(target = "owner", ignore = true)
    Task toEntity(TaskRequest request);

    /**
     * Applies non-null fields of the request onto an existing entity (partial update semantics).
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "owner", ignore = true)
    void updateEntity(TaskRequest request, @MappingTarget Task task);
}
