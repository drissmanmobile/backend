package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.StudentAnswerEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface SpringDataStudentAnswerRepository extends R2dbcRepository<StudentAnswerEntity, UUID> {
    Flux<StudentAnswerEntity> findByAttemptId(UUID attemptId);
}
