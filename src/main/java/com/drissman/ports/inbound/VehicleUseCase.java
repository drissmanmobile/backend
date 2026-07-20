package com.drissman.ports.inbound;

import com.drissman.domain.model.Vehicle;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface VehicleUseCase {
    Mono<Vehicle> createVehicle(Vehicle vehicle);
    Mono<Vehicle> updateVehicle(UUID id, Vehicle vehicle);
    Mono<Vehicle> getVehicle(UUID id);
    Flux<Vehicle> getVehiclesBySchool(UUID schoolId);
    Mono<Void> deleteVehicle(UUID id);
}
