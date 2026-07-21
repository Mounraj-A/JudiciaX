package com.courtai.ai.validator;

import com.courtai.ai.dto.GatewayResponse;
import com.courtai.ai.exception.AIResponseException;
import org.springframework.stereotype.Component;

@Component
public class ResponseValidator {

    public void validate(GatewayResponse<?> response, String expectedCorrelationId) {
        if (response == null) {
            throw new AIResponseException("AI service returned a null response");
        }
        
        if (response.getMetadata() == null) {
            throw new AIResponseException("AI response is missing metadata block");
        }

        if (!expectedCorrelationId.equals(response.getMetadata().getCorrelationId())) {
            throw new AIResponseException("Correlation ID mismatch in AI response");
        }

        if (response.getPayload() == null) {
            throw new AIResponseException("AI response payload is empty");
        }
    }
}
