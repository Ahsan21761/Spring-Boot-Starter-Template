package com.example.taskmanager.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;

/**
 * Declares the MySQL Testcontainer as a Spring-managed bean.
 *
 * <p>Because the bean lives in the (cached) application context, the container is started once
 * and shared across every integration-test class, instead of being stopped after each class —
 * which would leave a reused, cached context pointing at a dead container.
 *
 * <p>{@code @ServiceConnection} auto-configures Spring Boot's datasource from the running
 * container, so no manual URL/username/password wiring is needed.
 */
@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

    @Bean
    @ServiceConnection
    MySQLContainer<?> mysqlContainer() {
        return new MySQLContainer<>("mysql:8.0");
    }
}
