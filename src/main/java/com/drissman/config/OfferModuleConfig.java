package com.drissman.config;

import com.drissman.ports.inbound.OfferModuleUseCase;
import com.drissman.ports.outbound.ModuleRepositoryPort;
import com.drissman.ports.outbound.OfferModuleRepositoryPort;
import com.drissman.ports.outbound.OfferRepositoryPort;
import com.drissman.service.OfferModuleApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OfferModuleConfig {

    @Bean
    public OfferModuleUseCase offerModuleUseCase(
            OfferModuleRepositoryPort offerModuleRepository,
            ModuleRepositoryPort moduleRepository,
            OfferRepositoryPort offerRepository) {
        return new OfferModuleApplicationService(offerModuleRepository, moduleRepository, offerRepository);
    }
}
