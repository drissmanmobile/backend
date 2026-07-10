package com.drissman.ports.outbound;

import com.drissman.domain.model.SessionOffer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SessionOfferRepositoryPort {
    Flux<SessionOffer> findByTrainingPeriodId(UUID trainingPeriodId);
    Flux<SessionOffer> findByOfferId(UUID offerId);
    Mono<Void> deleteByTrainingPeriodId(UUID trainingPeriodId);
    Mono<Long> countByTrainingPeriodId(UUID trainingPeriodId);
    Mono<SessionOffer> save(SessionOffer sessionOffer);
    Mono<Void> delete(SessionOffer sessionOffer);
}
