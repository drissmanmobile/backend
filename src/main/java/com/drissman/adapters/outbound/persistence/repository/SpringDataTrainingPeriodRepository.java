package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.TrainingPeriodEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface SpringDataTrainingPeriodRepository extends R2dbcRepository<TrainingPeriodEntity, UUID> {
    Flux<TrainingPeriodEntity> findBySchoolId(UUID schoolId);
}
