package com.courtai.audit.service;

import com.courtai.audit.dto.AuditExportRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditExportServiceImpl implements AuditExportService {

    @Override
    public byte[] exportAuditLogs(AuditExportRequest request) {
        // Placeholder for real export logic generating PDF/CSV/Excel
        String mockCsv = "UUID,Action,Module,Timestamp\n" +
                         "123-abc,LOGIN,AUTH,2026-07-16\n";
        return mockCsv.getBytes(StandardCharsets.UTF_8);
    }
}
