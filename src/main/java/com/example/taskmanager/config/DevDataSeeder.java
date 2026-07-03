package com.example.taskmanager.config;

import com.example.taskmanager.task.Task;
import com.example.taskmanager.task.TaskRepository;
import com.example.taskmanager.task.TaskStatus;
import com.example.taskmanager.user.Role;
import com.example.taskmanager.user.User;
import com.example.taskmanager.user.UserRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds a demo admin account and sample tasks on startup — only when {@code app.seed.enabled=true}
 * and the users table is empty. Intended for local development and demos, never production.
 *
 * <p>Default credentials: {@code admin@example.com} / {@code admin12345}.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true")
public class DevDataSeeder {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    @org.springframework.context.annotation.Bean
    ApplicationRunner seedData() {
        return args -> seed();
    }

    @Transactional
    void seed() {
        if (userRepository.count() > 0) {
            log.info("Seed skipped: users already present.");
            return;
        }

        User admin = new User();
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("admin12345"));
        admin.setFullName("Demo Admin");
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);
        userRepository.save(admin);

        Task t1 = new Task();
        t1.setTitle("Explore the API in Swagger UI");
        t1.setDescription("Open /swagger-ui.html and authorize with the demo JWT.");
        t1.setStatus(TaskStatus.IN_PROGRESS);
        t1.setDueDate(LocalDate.now().plusDays(3));
        t1.setOwner(admin);

        Task t2 = new Task();
        t2.setTitle("Rename the base package");
        t2.setDescription("Replace com.example.taskmanager with your own group id.");
        t2.setStatus(TaskStatus.TODO);
        t2.setOwner(admin);

        taskRepository.save(t1);
        taskRepository.save(t2);

        log.info("Seeded demo admin (admin@example.com / admin12345) and {} sample tasks.", 2);
    }
}
