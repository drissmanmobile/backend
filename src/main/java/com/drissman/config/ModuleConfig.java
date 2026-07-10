package com.drissman.config;

import com.drissman.ports.inbound.ModuleUseCase;
import com.drissman.ports.outbound.ModuleRepositoryPort;
import com.drissman.service.ModuleApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModuleConfig {

    @Bean
    public ModuleUseCase moduleUseCase(ModuleRepositoryPort moduleRepositoryPort) {
        return new ModuleApplicationService(moduleRepositoryPort);
    }
}
