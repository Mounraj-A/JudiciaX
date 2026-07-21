package com.courtai.ai.ocr.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class OCRProfileRequest {
    private String documentUuid;
    private String caseUuid;
    private String userUuid;
    private MultipartFile file;
}
