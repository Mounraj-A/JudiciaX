package com.courtai.ai.registry;

import java.util.Optional;
import java.util.List;

public interface AIServiceRegistry {
    
    /**
     * Resolves a registered service by its internal ID (e.g., 'ocr', 'priority').
     */
    Optional<RegisteredService> getService(String serviceId);
    
    /**
     * Returns a list of all currently registered AI services.
     */
    List<RegisteredService> getAllServices();
}
