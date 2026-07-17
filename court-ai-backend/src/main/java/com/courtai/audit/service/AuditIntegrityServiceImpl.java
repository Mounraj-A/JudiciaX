package com.courtai.audit.service;

import com.courtai.audit.entity.AuditEvent;
import com.courtai.audit.entity.AuditIntegrity;
import com.courtai.audit.repository.AuditIntegrityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditIntegrityServiceImpl implements AuditIntegrityService {

    private final AuditIntegrityRepository auditIntegrityRepository;

    @Override
    @Transactional
    public void generateAndStoreHash(AuditEvent auditEvent) {
        try {
            Optional<AuditIntegrity> lastIntegrityOpt = auditIntegrityRepository.findTopByIsDeletedFalseOrderByCreatedAtDesc();
            String previousHash = lastIntegrityOpt.map(AuditIntegrity::getCurrentHash).orElse(null);

            String dataToHash = buildDataString(auditEvent) + (previousHash != null ? previousHash : "");
            String currentHash = generateSHA256(dataToHash);

            AuditIntegrity newIntegrity = AuditIntegrity.builder()
                    .auditEventUuid(auditEvent.getUuid())
                    .previousHash(previousHash)
                    .currentHash(currentHash)
                    .verificationStatus("VERIFIED")
                    .build();

            auditIntegrityRepository.save(newIntegrity);
        } catch (Exception e) {
            log.error("Failed to generate hash for audit event: {}", auditEvent.getUuid(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyIntegrity(String auditEventUuid) {
        // Simple mock for verification logic
        return auditIntegrityRepository.findByAuditEventUuidAndIsDeletedFalse(auditEventUuid).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyChain() {
        // Complete verification logic would go here
        List<AuditIntegrity> integrities = auditIntegrityRepository.findAll();
        // Just a mock implementation returning true for demo purposes
        return !integrities.isEmpty();
    }

    private String buildDataString(AuditEvent event) {
        return event.getUuid() + event.getAction() + event.getCorrelationId() + event.getTimestamp();
    }

    private String generateSHA256(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}
