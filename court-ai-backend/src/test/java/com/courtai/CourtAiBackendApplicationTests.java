package com.courtai;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Verifies that the Spring application context loads successfully.
 *
 * <p>This is the minimal smoke test that confirms all beans wire correctly
 * and no configuration errors exist. Uses H2 in-memory database for testing.</p>
 *
 * <p>{@code @ActiveProfiles("test")} automatically instructs Spring Boot to load
 * {@code application-test.yml} on top of {@code application.yml}.
 * Do NOT add {@code @TestPropertySource} for YAML files — it only supports
 * {@code .properties} files and will silently corrupt the property environment.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
class CourtAiBackendApplicationTests {

    @Test
    void contextLoads() {
        // If this test passes, the Spring application context loaded successfully.
        // No assertion needed — the test passes if no exception is thrown during context startup.
    }
}
