package com.courtai.ai.validator;

import com.courtai.ai.dto.GatewayRequest;
import com.courtai.ai.exception.AIValidationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RequestValidator {

    public void validate(GatewayRequest<?> request) {
        if (request == null) {
            throw new AIValidationException("GatewayRequest cannot be null");
        }
        
        if (request.getContext() == null) {
            throw new AIValidationException("AIRequestContext is missing from request");
        }
        
        if (!StringUtils.hasText(request.getContext().getCorrelationId())) {
            throw new AIValidationException("Correlation ID is required");
        }

        if (!StringUtils.hasText(request.getContext().getRequestId())) {
            throw new AIValidationException("Request ID is required");
        }
        
        if (!StringUtils.hasText(request.getTargetService())) {
            throw new AIValidationException("Target AI service must be specified");
        }

        if (request.getPayload() == null) {
            throw new AIValidationException("Request payload cannot be empty");
        }
    }
}
