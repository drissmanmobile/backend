package com.drissman.adapters.outbound.persistence;

import com.drissman.adapters.outbound.persistence.entity.OfferModuleEntity;
import com.drissman.adapters.outbound.persistence.mapper.OfferModulePersistenceMapper;
import com.drissman.adapters.outbound.persistence.repository.SpringDataOfferModuleRepository;
import com.drissman.domain.model.OfferModule;
import com.drissman.ports.outbound.OfferModuleRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OfferModulePersistenceAdapter implements OfferModuleRepositoryPort {

    private final SpringDataOfferModuleRepository repository;

    @Override
    public Flux<OfferModule> findByOfferIdOrderByOrderIndexAsc(UUID offerId) {
        return repository.findByOfferIdOrderByOrderIndexAsc(offerId)
                .map(OfferModulePersistenceMapper::toDomain);
    }

    @Override
    public Flux<OfferModule> findByModuleId(UUID moduleId) {
        return repository.findByModuleId(moduleId)
                .map(OfferModulePersistenceMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteByOfferId(UUID offerId) {
        return repository.deleteByOfferId(offerId);
    }

    @Override
    public Mono<OfferModule> findByOfferIdAndModuleId(UUID offerId, UUID moduleId) {
        return repository.findByOfferIdAndModuleId(offerId, moduleId)
                .map(OfferModulePersistenceMapper::toDomain);
    }

    @Override
    public Mono<OfferModule> save(OfferModule offerModule) {
        OfferModuleEntity entity = OfferModulePersistenceMapper.toEntity(offerModule);
        return repository.save(entity)
                .map(OfferModulePersistenceMapper::toDomain);
    }

    @Override
    public Mono<Void> delete(OfferModule offerModule) {
        OfferModuleEntity entity = OfferModulePersistenceMapper.toEntity(offerModule);
        return repository.delete(entity);
    }
}
