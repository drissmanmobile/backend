package com.drissman.config;

import com.drissman.ports.inbound.VehicleUseCase;
import com.drissman.ports.outbound.VehicleRepositoryPort;
import com.drissman.service.VehicleApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VehicleConfig {

    @Bean
    public VehicleUseCase vehicleUseCase(VehicleRepositoryPort vehicleRepositoryPort) {
        return new VehicleApplicationService(vehicleRepositoryPort);
    }
}
