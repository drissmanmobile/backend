package com.drissman.ports.outbound;

import com.drissman.domain.model.TrainingPeriod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TrainingPeriodRepositoryPort {
    Flux<TrainingPeriod> findBySchoolId(UUID schoolId);
    Mono<TrainingPeriod> findById(UUID id);
    Mono<TrainingPeriod> save(TrainingPeriod period);
    Mono<Void> delete(TrainingPeriod period);
}
