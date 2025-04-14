package com.example.carservice.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Sercan Noyan Germiyanoğlu",
                        url = "https://github.com/Rapter1990/carservice"
                ),
                description = "Case Study - Car Service" +
                        " (Java 21, Spring Boot, MySql, JUnit, Spring Security, JWT, Docker, Kubernetes, Prometheus, Grafana, Github Actions (CI/CD) ) ",
                title = "carservice",
                version = "1.0.0"
        )
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Token",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {

}
