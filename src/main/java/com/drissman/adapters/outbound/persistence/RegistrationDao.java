package com.drissman.adapters.outbound.persistence;

import com.drissman.adapters.outbound.persistence.entity.RegistrationEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface RegistrationDao extends ReactiveCrudRepository<RegistrationEntity, UUID> {
    Flux<RegistrationEntity> findAllByUserId(UUID userId);
    Flux<RegistrationEntity> findAllBySchoolId(UUID schoolId);
}
