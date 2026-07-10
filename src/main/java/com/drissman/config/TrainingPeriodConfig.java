package com.drissman.config;

import com.drissman.ports.inbound.TrainingPeriodUseCase;
import com.drissman.ports.outbound.EnrollmentRepositoryPort;
import com.drissman.ports.outbound.OfferRepositoryPort;
import com.drissman.ports.outbound.TrainingPeriodRepositoryPort;
import com.drissman.ports.outbound.SessionOfferRepositoryPort;
import com.drissman.service.TrainingPeriodApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TrainingPeriodConfig {

    @Bean
    public TrainingPeriodUseCase trainingPeriodUseCase(TrainingPeriodRepositoryPort trainingPeriodRepositoryPort,
                                                       SessionOfferRepositoryPort sessionOfferRepository,
                                                       OfferRepositoryPort offerRepositoryPort,
                                                       EnrollmentRepositoryPort enrollmentRepositoryPort) {
        return new TrainingPeriodApplicationService(
                trainingPeriodRepositoryPort,
                sessionOfferRepository,
                offerRepositoryPort,
                enrollmentRepositoryPort
        );
    }
}
