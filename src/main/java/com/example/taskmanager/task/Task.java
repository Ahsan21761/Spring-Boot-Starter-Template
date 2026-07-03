package com.example.taskmanager.task;

import com.example.taskmanager.common.domain.BaseEntity;
import com.example.taskmanager.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A unit of work owned by a {@link User}.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tasks")
public class Task extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private TaskStatus status = TaskStatus.TODO;

    @Column(name = "due_date")
    private LocalDate dueDate;

    /** Owner of the task. Lazily fetched; ownership is enforced in the service layer. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_tasks_owner"))
    private User owner;
}
