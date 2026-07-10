package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.SessionOfferEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface SpringDataSessionOfferRepository extends ReactiveCrudRepository<SessionOfferEntity, UUID> {

    Flux<SessionOfferEntity> findByTrainingPeriodId(UUID trainingPeriodId);

    Flux<SessionOfferEntity> findByOfferId(UUID offerId);

    Mono<Void> deleteByTrainingPeriodId(UUID trainingPeriodId);

    Mono<Long> countByTrainingPeriodId(UUID trainingPeriodId);
}
