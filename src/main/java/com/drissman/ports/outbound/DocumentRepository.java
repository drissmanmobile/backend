package com.drissman.ports.outbound;

import com.drissman.domain.model.Document;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface DocumentRepository {
    Mono<Document> save(Document document);
    Flux<Document> findByModuleId(UUID moduleId);
    Flux<Document> findBySessionId(UUID sessionId);
    Flux<Document> findBySchoolId(UUID schoolId);
    Flux<Document> findByOfferId(UUID offerId);
    Mono<Document> findById(UUID id);
    Mono<Void> deleteById(UUID id);
}
