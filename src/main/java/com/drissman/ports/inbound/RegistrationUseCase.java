package com.drissman.ports.inbound;

import com.drissman.domain.model.Registration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RegistrationUseCase {
    Mono<Registration> createRegistration(UUID userId, UUID schoolId, UUID formationId, String category, String remarks, String phone, String cniNumber, String birthDate, String address);
    Mono<Registration> getRegistration(UUID id);
    Flux<Registration> getMyRegistrations(UUID userId);
    Flux<Registration> getAdminRegistrations(UUID schoolId);
    Mono<Registration> approveRegistration(UUID id, UUID adminId, String remarks, UUID monitorId);
    Mono<Registration> rejectRegistration(UUID id, UUID adminId, String remarks);
}
