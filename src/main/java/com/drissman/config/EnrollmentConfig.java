package com.drissman.config;

import com.drissman.ports.outbound.InvoiceRepositoryPort;
import com.drissman.ports.outbound.UserRepositoryPort;
import com.drissman.ports.inbound.EnrollmentUseCase;
import com.drissman.ports.outbound.EnrollmentRepositoryPort;
import com.drissman.ports.outbound.OfferRepositoryPort;
import com.drissman.service.EnrollmentApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnrollmentConfig {

    @Bean
    public EnrollmentUseCase enrollmentUseCase(
            EnrollmentRepositoryPort enrollmentRepositoryPort,
            OfferRepositoryPort offerRepositoryPort,
            UserRepositoryPort userRepository,
            InvoiceRepositoryPort invoiceRepositoryPort) {
        return new EnrollmentApplicationService(enrollmentRepositoryPort, offerRepositoryPort, userRepository, invoiceRepositoryPort);
    }
}
