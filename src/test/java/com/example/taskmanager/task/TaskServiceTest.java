package com.example.taskmanager.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.taskmanager.common.exception.ResourceNotFoundException;
import com.example.taskmanager.task.dto.TaskRequest;
import com.example.taskmanager.task.dto.TaskResponse;
import com.example.taskmanager.user.User;
import com.example.taskmanager.user.UserRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link TaskService} verifying ownership scoping and defaulting — no Spring, no Docker.
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock private TaskRepository taskRepository;
    @Mock private UserRepository userRepository;
    @Mock private TaskMapper taskMapper;

    @InjectMocks private TaskService taskService;

    private static final Long OWNER_ID = 7L;

    @Test
    void createAssignsOwnerAndDefaultsStatusToTodo() {
        TaskRequest request = new TaskRequest("Title", "Desc", null, null);
        User owner = new User();
        when(userRepository.getReferenceById(OWNER_ID)).thenReturn(owner);
        when(taskMapper.toEntity(request)).thenReturn(new Task());
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));
        when(taskMapper.toResponse(any(Task.class))).thenReturn(sampleResponse());

        taskService.create(OWNER_ID, request);

        ArgumentCaptor<Task> saved = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(saved.capture());
        assertThat(saved.getValue().getOwner()).isSameAs(owner);
        assertThat(saved.getValue().getStatus()).isEqualTo(TaskStatus.TODO);
    }

    @Test
    void getReturnsMappedTaskWhenOwned() {
        Task task = new Task();
        when(taskRepository.findByIdAndOwnerId(1L, OWNER_ID)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(sampleResponse());

        TaskResponse result = taskService.get(OWNER_ID, 1L);

        assertThat(result).isNotNull();
    }

    @Test
    void getThrowsWhenTaskNotOwnedOrMissing() {
        when(taskRepository.findByIdAndOwnerId(99L, OWNER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.get(OWNER_ID, 99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteRemovesOwnedTask() {
        Task task = new Task();
        when(taskRepository.findByIdAndOwnerId(1L, OWNER_ID)).thenReturn(Optional.of(task));

        taskService.delete(OWNER_ID, 1L);

        verify(taskRepository).delete(task);
    }

    @Test
    void deleteDoesNothingDestructiveWhenNotOwned() {
        when(taskRepository.findByIdAndOwnerId(5L, OWNER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.delete(OWNER_ID, 5L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(taskRepository, never()).delete(any());
    }

    @Test
    void updateAppliesMapperOntoOwnedEntity() {
        Task task = new Task();
        TaskRequest request = new TaskRequest("New", null, TaskStatus.DONE, null);
        when(taskRepository.findByIdAndOwnerId(1L, OWNER_ID)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(sampleResponse());

        taskService.update(OWNER_ID, 1L, request);

        verify(taskMapper).updateEntity(eq(request), eq(task));
        verify(taskRepository).save(task);
    }

    private TaskResponse sampleResponse() {
        return new TaskResponse(1L, "Title", "Desc", TaskStatus.TODO, null, OWNER_ID,
                Instant.now(), Instant.now());
    }
}
