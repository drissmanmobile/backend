package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.SessionLocationEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface SpringDataSessionLocationRepository extends R2dbcRepository<SessionLocationEntity, UUID> {
    Mono<SessionLocationEntity> findTopBySessionIdOrderByCreatedAtDesc(UUID sessionId);
    Flux<SessionLocationEntity> findBySessionIdOrderByCreatedAtAsc(UUID sessionId);
}
