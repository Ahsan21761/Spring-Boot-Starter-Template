package com.example.taskmanager;

import com.example.taskmanager.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

/**
 * Smoke test: verifies the full Spring context (JPA, Flyway, Security, MapStruct beans) starts
 * against a real MySQL database.
 */
class ApplicationContextIT extends AbstractIntegrationTest {

    @Test
    void contextLoads() {
        // Success is the context starting without error.
    }
}
