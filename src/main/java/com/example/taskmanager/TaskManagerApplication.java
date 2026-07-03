package com.example.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Application entry point.
 *
 * <p>This is a reusable, production-oriented Spring Boot starter template. The example
 * domain (users + tasks) exists only to demonstrate a clean, layered architecture; replace
 * it with your own domain when reusing this template.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class TaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }
}
