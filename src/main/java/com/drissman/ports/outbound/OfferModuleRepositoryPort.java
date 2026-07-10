package com.drissman.ports.outbound;

import com.drissman.domain.model.OfferModule;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OfferModuleRepositoryPort {
    Flux<OfferModule> findByOfferIdOrderByOrderIndexAsc(UUID offerId);
    Flux<OfferModule> findByModuleId(UUID moduleId);
    Mono<Void> deleteByOfferId(UUID offerId);
    Mono<OfferModule> findByOfferIdAndModuleId(UUID offerId, UUID moduleId);
    Mono<OfferModule> save(OfferModule offerModule);
    Mono<Void> delete(OfferModule offerModule);
}
