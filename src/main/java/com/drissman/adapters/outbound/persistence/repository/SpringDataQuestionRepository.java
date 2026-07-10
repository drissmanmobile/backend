package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.QuestionEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface SpringDataQuestionRepository extends R2dbcRepository<QuestionEntity, UUID> {
    Flux<QuestionEntity> findByExerciseId(UUID exerciseId);
}
