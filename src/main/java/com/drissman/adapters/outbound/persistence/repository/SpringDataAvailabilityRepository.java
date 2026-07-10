package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.AvailabilityEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface SpringDataAvailabilityRepository extends ReactiveCrudRepository<AvailabilityEntity, UUID> {
    Flux<AvailabilityEntity> findBySchoolId(UUID schoolId);

    Flux<AvailabilityEntity> findBySchoolIdAndDayOfWeek(UUID schoolId, Integer dayOfWeek);
}
