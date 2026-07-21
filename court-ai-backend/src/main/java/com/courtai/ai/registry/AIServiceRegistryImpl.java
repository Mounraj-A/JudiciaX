package com.courtai.ai.registry;

import com.courtai.ai.config.AIProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AIServiceRegistryImpl implements AIServiceRegistry {

    private final AIProperties properties;

    @Override
    public Optional<RegisteredService> getService(String serviceId) {
        if (properties.getRegistry() == null || properties.getRegistry().getServices() == null) {
            return Optional.empty();
        }
        
        var config = properties.getRegistry().getServices().get(serviceId);
        if (config == null) {
            return Optional.empty();
        }
        
        return Optional.of(RegisteredService.builder()
                .serviceId(serviceId)
                .name(config.getName())
                .version(config.getVersion())
                .endpointUrl(config.getEndpoint())
                .enabled(config.isEnabled())
                .timeoutMs(config.getTimeout())
                .status(config.getStatus())
                .build());
    }

    @Override
    public List<RegisteredService> getAllServices() {
        List<RegisteredService> list = new ArrayList<>();
        if (properties.getRegistry() != null && properties.getRegistry().getServices() != null) {
            properties.getRegistry().getServices().forEach((key, config) -> {
                list.add(RegisteredService.builder()
                        .serviceId(key)
                        .name(config.getName())
                        .version(config.getVersion())
                        .endpointUrl(config.getEndpoint())
                        .enabled(config.isEnabled())
                        .timeoutMs(config.getTimeout())
                        .status(config.getStatus())
                        .build());
            });
        }
        return list;
    }
}
