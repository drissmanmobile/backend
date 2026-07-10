package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.DocumentEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface SpringDataDocumentRepository extends ReactiveCrudRepository<DocumentEntity, UUID> {
    Flux<DocumentEntity> findByModuleId(UUID moduleId);
    Flux<DocumentEntity> findBySessionId(UUID sessionId);
    Flux<DocumentEntity> findBySchoolId(UUID schoolId);
    Flux<DocumentEntity> findByOfferId(UUID offerId);
}
