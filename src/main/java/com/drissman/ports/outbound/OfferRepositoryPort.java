package com.drissman.ports.outbound;

import com.drissman.domain.model.Offer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OfferRepositoryPort {
    Mono<Offer> findById(UUID id);
    Flux<Offer> findBySchoolId(UUID schoolId);
    Mono<Offer> save(Offer offer);
    Mono<Void> delete(Offer offer);
}
