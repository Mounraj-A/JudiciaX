package com.courtai.ai.registry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisteredService {
    private String serviceId;
    private String name;
    private String version;
    private String endpointUrl;
    private boolean enabled;
    private int timeoutMs;
    private String status;
}
