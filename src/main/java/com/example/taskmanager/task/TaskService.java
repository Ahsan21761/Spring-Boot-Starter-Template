package com.example.taskmanager.task;

import com.example.taskmanager.common.exception.ResourceNotFoundException;
import com.example.taskmanager.task.dto.TaskRequest;
import com.example.taskmanager.task.dto.TaskResponse;
import com.example.taskmanager.user.User;
import com.example.taskmanager.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic for tasks. Every operation is scoped to the authenticated owner: a user can
 * only ever see or mutate their own tasks, which is enforced here rather than in the controller.
 */
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Transactional(readOnly = true)
    public Page<TaskResponse> list(Long ownerId, TaskStatus status, Pageable pageable) {
        Page<Task> tasks = (status == null)
                ? taskRepository.findByOwnerId(ownerId, pageable)
                : taskRepository.findByOwnerIdAndStatus(ownerId, status, pageable);
        return tasks.map(taskMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public TaskResponse get(Long ownerId, Long taskId) {
        return taskMapper.toResponse(requireOwnedTask(ownerId, taskId));
    }

    @Transactional
    public TaskResponse create(Long ownerId, TaskRequest request) {
        User owner = userRepository.getReferenceById(ownerId);
        Task task = taskMapper.toEntity(request);
        task.setOwner(owner);
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.TODO);
        }
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse update(Long ownerId, Long taskId, TaskRequest request) {
        Task task = requireOwnedTask(ownerId, taskId);
        taskMapper.updateEntity(request, task);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public void delete(Long ownerId, Long taskId) {
        Task task = requireOwnedTask(ownerId, taskId);
        taskRepository.delete(task);
    }

    private Task requireOwnedTask(Long ownerId, Long taskId) {
        return taskRepository.findByIdAndOwnerId(taskId, ownerId)
                .orElseThrow(() -> ResourceNotFoundException.of("Task", taskId));
    }
}
