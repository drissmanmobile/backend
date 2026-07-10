package com.drissman.adapters.outbound.persistence;

import com.drissman.adapters.outbound.persistence.entity.SessionOfferEntity;
import com.drissman.adapters.outbound.persistence.mapper.SessionOfferPersistenceMapper;
import com.drissman.adapters.outbound.persistence.repository.SpringDataSessionOfferRepository;
import com.drissman.domain.model.SessionOffer;
import com.drissman.ports.outbound.SessionOfferRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SessionOfferPersistenceAdapter implements SessionOfferRepositoryPort {

    private final SpringDataSessionOfferRepository repository;

    @Override
    public Flux<SessionOffer> findByTrainingPeriodId(UUID trainingPeriodId) {
        return repository.findByTrainingPeriodId(trainingPeriodId)
                .map(SessionOfferPersistenceMapper::toDomain);
    }

    @Override
    public Flux<SessionOffer> findByOfferId(UUID offerId) {
        return repository.findByOfferId(offerId)
                .map(SessionOfferPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteByTrainingPeriodId(UUID trainingPeriodId) {
        return repository.deleteByTrainingPeriodId(trainingPeriodId);
    }

    @Override
    public Mono<Long> countByTrainingPeriodId(UUID trainingPeriodId) {
        return repository.countByTrainingPeriodId(trainingPeriodId);
    }

    @Override
    public Mono<SessionOffer> save(SessionOffer sessionOffer) {
        SessionOfferEntity entity = SessionOfferPersistenceMapper.toEntity(sessionOffer);
        return repository.save(entity)
                .map(SessionOfferPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Void> delete(SessionOffer sessionOffer) {
        SessionOfferEntity entity = SessionOfferPersistenceMapper.toEntity(sessionOffer);
        return repository.delete(entity);
    }
}
