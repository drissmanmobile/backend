package com.drissman.ports.outbound;

import com.drissman.domain.model.Availability;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AvailabilityRepositoryPort {
    Flux<Availability> findBySchoolId(UUID schoolId);
    Flux<Availability> findBySchoolIdAndDayOfWeek(UUID schoolId, Integer dayOfWeek);
    Mono<Availability> findById(UUID id);
    Mono<Availability> save(Availability availability);
    Mono<Void> deleteById(UUID id);
}
