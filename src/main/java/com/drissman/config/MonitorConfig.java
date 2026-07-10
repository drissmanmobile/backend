package com.drissman.config;

import com.drissman.ports.outbound.UserRepositoryPort;
import com.drissman.ports.inbound.MonitorUseCase;
import com.drissman.ports.outbound.MonitorRepositoryPort;
import com.drissman.service.MonitorApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class MonitorConfig {

    @Bean
    public MonitorUseCase monitorUseCase(
            MonitorRepositoryPort monitorRepositoryPort,
            UserRepositoryPort userRepository,
            PasswordEncoder passwordEncoder) {
        return new MonitorApplicationService(monitorRepositoryPort, userRepository, passwordEncoder);
    }
}
