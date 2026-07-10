package com.drissman.config;

import com.drissman.ports.inbound.SchoolUseCase;
import com.drissman.ports.outbound.OfferRepositoryPort;
import com.drissman.ports.outbound.SchoolRepositoryPort;
import com.drissman.service.SchoolApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchoolConfig {

    @Bean
    public SchoolUseCase schoolUseCase(SchoolRepositoryPort schoolRepositoryPort, OfferRepositoryPort offerRepositoryPort) {
        return new SchoolApplicationService(schoolRepositoryPort, offerRepositoryPort);
    }
}
