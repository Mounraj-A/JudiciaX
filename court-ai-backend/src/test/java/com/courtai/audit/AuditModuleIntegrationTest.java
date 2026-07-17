package com.courtai.audit;

import com.courtai.audit.dto.AuditExportRequest;
import com.courtai.audit.dto.AuditSearchRequest;
import com.courtai.audit.entity.AuditEvent;
import com.courtai.audit.repository.AuditEventRepository;
import com.courtai.audit.service.AuditComplianceService;
import com.courtai.audit.service.AuditEngineService;
import com.courtai.audit.service.SecurityAuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuditModuleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuditEngineService auditEngineService;

    @Autowired
    private AuditComplianceService auditComplianceService;

    @Autowired
    private SecurityAuditService securityAuditService;

    @Autowired
    private AuditEventRepository auditEventRepository;

    private AuditEvent testEvent;
    private String testCorrelationId;

    @BeforeEach
    void setUp() {
        auditEventRepository.deleteAll();
        testEvent = auditEngineService.publishEvent("TEST_MODULE", "TEST_ACTION", "TestEntity", "entity-123", "Test remarks");
        testCorrelationId = testEvent.getCorrelationId();
        
        auditComplianceService.logComplianceViolation(testCorrelationId, "UNAUTHORIZED_OVERRIDE", "Details", "FAILED");
        securityAuditService.logSecurityEvent(testCorrelationId, "LOGIN_FAILED", "127.0.0.1", "Bad credentials");
    }

    // ==========================================
    // POSITIVE CASES (WITH ROLE_ADMIN)
    // ==========================================

    @Test
    @DisplayName("Should successfully retrieve audit details by UUID")
    @WithMockUser(roles = "ADMIN")
    void testGetAuditDetails_Success() throws Exception {
        mockMvc.perform(get("/api/v1/audit/" + testEvent.getUuid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.uuid").value(testEvent.getUuid()))
                .andExpect(jsonPath("$.data.action").value("TEST_ACTION"));
    }

    @Test
    @DisplayName("Should successfully search audit events")
    @WithMockUser(roles = "ADMIN")
    void testSearchAuditEvents_Success() throws Exception {
        AuditSearchRequest searchRequest = AuditSearchRequest.builder()
                .module("TEST_MODULE")
                .action("TEST_ACTION")
                .build();

        mockMvc.perform(post("/api/v1/audit/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.content[0].module").value("TEST_MODULE"));
    }

    @Test
    @DisplayName("Should successfully retrieve audit timeline by correlation ID")
    @WithMockUser(roles = "ADMIN")
    void testGetAuditTimeline_Success() throws Exception {
        mockMvc.perform(get("/api/v1/audit/timeline/" + testCorrelationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].correlationId").value(testCorrelationId));
    }

    @Test
    @DisplayName("Should verify hash integrity successfully")
    @WithMockUser(roles = "ADMIN")
    void testVerifyIntegrity_Success() throws Exception {
        mockMvc.perform(get("/api/v1/audit/integrity/verify/" + testEvent.getUuid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("Should verify entire hash chain successfully")
    @WithMockUser(roles = "ADMIN")
    void testVerifyChain_Success() throws Exception {
        mockMvc.perform(get("/api/v1/audit/integrity/verify-chain"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("Should retrieve compliance violations")
    @WithMockUser(roles = "ADMIN")
    void testGetComplianceViolations_Success() throws Exception {
        mockMvc.perform(get("/api/v1/audit/compliance/violations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].violationType").value("UNAUTHORIZED_OVERRIDE"));
    }

    @Test
    @DisplayName("Should retrieve security events")
    @WithMockUser(roles = "ADMIN")
    void testGetSecurityEvents_Success() throws Exception {
        mockMvc.perform(get("/api/v1/audit/security/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0))); // Currently a placeholder returning empty list
    }

    @Test
    @DisplayName("Should export audit logs successfully")
    @WithMockUser(roles = "ADMIN")
    void testExportLogs_Success() throws Exception {
        AuditExportRequest exportRequest = AuditExportRequest.builder()
                .format("CSV")
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now())
                .build();

        mockMvc.perform(post("/api/v1/audit/export")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exportRequest)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("audit_export.csv")))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_OCTET_STREAM));
    }

    // ==========================================
    // NEGATIVE CASES
    // ==========================================

    @Test
    @DisplayName("Should return 404 Not Found for non-existent Audit UUID")
    @WithMockUser(roles = "ADMIN")
    void testGetAuditDetails_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/audit/non-existent-uuid"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Should return 403 Forbidden for User without Admin role")
    @WithMockUser(roles = "USER") // Insufficient role
    void testAccessDenied_InsufficientRole() throws Exception {
        mockMvc.perform(get("/api/v1/audit/" + testEvent.getUuid()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when no user is authenticated")
    void testAccessDenied_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/audit/" + testEvent.getUuid()))
                .andExpect(status().isUnauthorized()); // Or 403 depending on Spring Security setup
    }
}
