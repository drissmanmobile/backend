package com.drissman.config;

import com.drissman.ports.inbound.UserUseCase;
import com.drissman.ports.outbound.UserRepositoryPort;
import com.drissman.service.UserApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserConfig {

    @Bean
    public UserUseCase userUseCase(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder) {
        return new UserApplicationService(userRepositoryPort, passwordEncoder);
    }
}
