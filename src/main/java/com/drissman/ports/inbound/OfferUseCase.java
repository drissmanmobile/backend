package com.drissman.ports.inbound;

import com.drissman.domain.model.Offer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OfferUseCase {
    Mono<Offer> create(UUID schoolId, String name, String description, Integer price, Integer hours, String permitType);
    Flux<Offer> findBySchoolId(UUID schoolId);
    Mono<Offer> findById(UUID id);
    Mono<Offer> update(UUID schoolId, UUID id, String name, String description, Integer price, Integer hours, String permitType);
    Mono<Void> delete(UUID schoolId, UUID id);
}
