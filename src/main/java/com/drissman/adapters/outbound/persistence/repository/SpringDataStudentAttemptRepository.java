package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.StudentAttemptEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface SpringDataStudentAttemptRepository extends R2dbcRepository<StudentAttemptEntity, UUID> {
    Flux<StudentAttemptEntity> findByStudentId(UUID studentId);
    Flux<StudentAttemptEntity> findByExerciseId(UUID exerciseId);
}
