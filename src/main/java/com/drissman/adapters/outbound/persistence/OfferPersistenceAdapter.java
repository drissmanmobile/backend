package com.drissman.adapters.outbound.persistence;

import com.drissman.adapters.outbound.persistence.mapper.OfferPersistenceMapper;
import com.drissman.adapters.outbound.persistence.repository.SpringDataOfferRepository;
import com.drissman.domain.model.Offer;
import com.drissman.ports.outbound.OfferRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OfferPersistenceAdapter implements OfferRepositoryPort {

    private final SpringDataOfferRepository springDataOfferRepository;

    @Override
    public Mono<Offer> findById(UUID id) {
        return springDataOfferRepository.findById(id)
                .map(OfferPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Offer> findBySchoolId(UUID schoolId) {
        return springDataOfferRepository.findBySchoolId(schoolId)
                .map(OfferPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Offer> save(Offer offer) {
        return springDataOfferRepository.save(OfferPersistenceMapper.toEntity(offer))
                .map(OfferPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Void> delete(Offer offer) {
        return springDataOfferRepository.delete(OfferPersistenceMapper.toEntity(offer));
    }
}
