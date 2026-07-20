package com.drissman.ports.outbound;

import com.drissman.domain.model.Vehicle;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface VehicleRepositoryPort {
    Mono<Vehicle> save(Vehicle vehicle);
    Mono<Vehicle> findById(UUID id);
    Flux<Vehicle> findBySchoolId(UUID schoolId);
    Mono<Void> deleteById(UUID id);
}
