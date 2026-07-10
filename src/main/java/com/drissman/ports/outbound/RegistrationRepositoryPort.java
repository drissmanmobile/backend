package com.drissman.ports.outbound;

import com.drissman.domain.model.Registration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RegistrationRepositoryPort {
    Mono<Registration> save(Registration registration);
    Mono<Registration> findById(UUID id);
    Flux<Registration> findAllByUserId(UUID userId);
    Flux<Registration> findAllBySchoolId(UUID schoolId);
}
