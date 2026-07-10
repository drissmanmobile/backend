package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.ChoiceEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface SpringDataChoiceRepository extends R2dbcRepository<ChoiceEntity, UUID> {
    Flux<ChoiceEntity> findByQuestionId(UUID questionId);
}
