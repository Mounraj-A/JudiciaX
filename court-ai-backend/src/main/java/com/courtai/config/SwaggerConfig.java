package com.courtai.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3 / Swagger UI configuration.
 *
 * <p>Configures the API documentation with JWT Bearer security scheme,
 * server URLs, contact information, and license details.</p>
 *
 * <p>Swagger UI is available at: {@code /api/v1/swagger-ui.html}</p>
 * <p>OpenAPI JSON is available at: {@code /api/v1/api-docs}</p>
 */
@Configuration
public class SwaggerConfig {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Bean
    public OpenAPI courtAiOpenAPI() {
        return new OpenAPI()
                .info(buildApiInfo())
                .servers(buildServers())
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH_SCHEME, buildJwtSecurityScheme()));
    }

    private Info buildApiInfo() {
        return new Info()
                .title("Court AI Backend — Judicial Case Management API")
                .version(appVersion)
                .description("""
                        ## AI-Powered Trusted Judicial Case Management & Prioritization System
                        
                        This API powers the backend of an enterprise judicial case management platform
                        with AI-driven prioritization, role-based access control, audit trails, 
                        and document management.
                        
                        ### Authentication
                        Use the **Authorize** button to provide your JWT Bearer token.
                        Format: `Bearer <your_token>`
                        
                        ### Roles
                        - `ROLE_ADMIN` — Full system access
                        - `ROLE_JUDGE` — Case management and verdicts
                        - `ROLE_CLERK` — Case filing and document management
                        - `ROLE_ADVOCATE` — Case viewing and submissions
                        """)
                .contact(new Contact()
                        .name("Court AI Development Team")
                        .email("dev@courtai.com")
                        .url("https://courtai.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    private List<Server> buildServers() {
        return List.of(
                new Server()
                        .url("http://localhost:8080/api/v1")
                        .description("Local Development Server"),
                new Server()
                        .url("https://api.courtai.com/api/v1")
                        .description("Production Server")
        );
    }

    private SecurityScheme buildJwtSecurityScheme() {
        return new SecurityScheme()
                .name(BEARER_AUTH_SCHEME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Enter JWT token in format: Bearer {token}");
    }
}
