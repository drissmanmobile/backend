package com.drissman.config;

import com.drissman.ports.inbound.ReviewUseCase;
import com.drissman.ports.outbound.EnrollmentRepositoryPort;
import com.drissman.ports.outbound.ReviewRepositoryPort;
import com.drissman.ports.outbound.SchoolRepositoryPort;
import com.drissman.ports.outbound.UserRepositoryPort;
import com.drissman.service.ReviewApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReviewConfig {

    @Bean
    public ReviewUseCase reviewUseCase(ReviewRepositoryPort reviewRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            SchoolRepositoryPort schoolRepositoryPort,
            EnrollmentRepositoryPort enrollmentRepositoryPort) {
        return new ReviewApplicationService(
                reviewRepositoryPort,
                userRepositoryPort,
                schoolRepositoryPort,
                enrollmentRepositoryPort);
    }
}
