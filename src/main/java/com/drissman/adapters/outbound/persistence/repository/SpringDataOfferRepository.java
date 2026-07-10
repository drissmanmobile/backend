package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.OfferEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface SpringDataOfferRepository extends ReactiveCrudRepository<OfferEntity, UUID> {
    Flux<OfferEntity> findBySchoolId(UUID schoolId);
}
