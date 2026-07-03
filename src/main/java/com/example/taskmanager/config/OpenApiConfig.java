package com.example.taskmanager.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger metadata. A "bearerAuth" scheme is declared so the Swagger UI shows an
 * Authorize button for pasting a JWT. UI is served at {@code /swagger-ui.html}.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Spring Boot Starter Template API",
                version = "0.1.0",
                description = "Reference REST API demonstrating production-ready backend practices.",
                contact = @Contact(name = "Maintainer", url = "https://github.com/your-username/spring-boot-starter-template"),
                license = @License(name = "MIT", url = "https://opensource.org/licenses/MIT")))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT")
public class OpenApiConfig {
}
