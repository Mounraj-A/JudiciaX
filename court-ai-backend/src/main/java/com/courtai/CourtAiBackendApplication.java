package com.courtai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

/**
 * Entry point for the AI-Powered Judicial Case Management & Prioritization System.
 *
 * <p>This application serves as the backend foundation for managing judicial cases,
 * prioritization using AI, secure role-based access, and audit trails.</p>
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EnableScheduling
@EnableAsync
public class CourtAiBackendApplication {

    public static void main(String[] args) {
        // Enforce UTC timezone globally at JVM startup
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(CourtAiBackendApplication.class, args);
    }
}
