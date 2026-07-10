package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.mapper.DocumentPersistenceMapper;
import com.drissman.domain.model.Document;
import com.drissman.ports.outbound.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DocumentRepositoryImpl implements DocumentRepository {

    private final SpringDataDocumentRepository repository;
    private final DocumentPersistenceMapper mapper;

    @Override
    public Mono<Document> save(Document document) {
        return repository.save(mapper.toEntity(document))
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Document> findByModuleId(UUID moduleId) {
        return repository.findByModuleId(moduleId)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Document> findBySessionId(UUID sessionId) {
        return repository.findBySessionId(sessionId)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Document> findBySchoolId(UUID schoolId) {
        return repository.findBySchoolId(schoolId)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Document> findByOfferId(UUID offerId) {
        return repository.findByOfferId(offerId)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Document> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }
}
