package com.example.taskmanager.support;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for full-stack integration tests. A single MySQL container (see
 * {@link ContainersConfig}) backs the whole suite via {@code @ServiceConnection}, and Flyway
 * migrations run against it — so tests exercise the real production schema.
 *
 * <p>{@code disabledWithoutDocker = true} cleanly skips these tests on machines without Docker
 * while running them in CI where Docker is available.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Import(ContainersConfig.class)
@Testcontainers(disabledWithoutDocker = true)
public abstract class AbstractIntegrationTest {
}
