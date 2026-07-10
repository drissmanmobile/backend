package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.ExerciseEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface SpringDataExerciseRepository extends R2dbcRepository<ExerciseEntity, UUID> {
    Flux<ExerciseEntity> findByStudentId(UUID studentId);
}
