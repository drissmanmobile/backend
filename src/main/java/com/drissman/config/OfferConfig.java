package com.drissman.config;

import com.drissman.ports.inbound.OfferUseCase;
import com.drissman.ports.outbound.OfferRepositoryPort;
import com.drissman.service.OfferApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OfferConfig {

    @Bean
    public OfferUseCase offerUseCase(OfferRepositoryPort offerRepositoryPort) {
        return new OfferApplicationService(offerRepositoryPort);
    }
}
