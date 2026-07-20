package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.VehicleEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface SpringDataVehicleRepository extends ReactiveCrudRepository<VehicleEntity, UUID> {
    Flux<VehicleEntity> findBySchoolId(UUID schoolId);
}
