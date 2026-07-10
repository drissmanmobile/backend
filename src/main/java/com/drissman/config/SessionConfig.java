package com.drissman.config;

import com.drissman.ports.inbound.SessionUseCase;
import com.drissman.ports.outbound.EnrollmentRepositoryPort;
import com.drissman.ports.outbound.MonitorRepositoryPort;
import com.drissman.ports.outbound.OfferRepositoryPort;
import com.drissman.ports.outbound.TrainingPeriodRepositoryPort;
import com.drissman.ports.outbound.SessionRepositoryPort;
import com.drissman.service.SessionApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionConfig {

    @Bean
    public SessionUseCase sessionUseCase(
            SessionRepositoryPort sessionRepositoryPort,
            EnrollmentRepositoryPort enrollmentRepositoryPort,
            MonitorRepositoryPort monitorRepositoryPort,
            OfferRepositoryPort offerRepositoryPort,
            TrainingPeriodRepositoryPort trainingPeriodRepository) {
        return new SessionApplicationService(
                sessionRepositoryPort,
                enrollmentRepositoryPort,
                monitorRepositoryPort,
                offerRepositoryPort,
                trainingPeriodRepository
        );
    }
}
