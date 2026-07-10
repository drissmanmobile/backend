package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.OfferModuleEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface SpringDataOfferModuleRepository extends ReactiveCrudRepository<OfferModuleEntity, UUID> {

    Flux<OfferModuleEntity> findByOfferIdOrderByOrderIndexAsc(UUID offerId);

    Flux<OfferModuleEntity> findByModuleId(UUID moduleId);

    Mono<Void> deleteByOfferId(UUID offerId);

    Mono<OfferModuleEntity> findByOfferIdAndModuleId(UUID offerId, UUID moduleId);
}
