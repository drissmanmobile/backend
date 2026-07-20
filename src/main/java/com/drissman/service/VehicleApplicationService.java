package com.drissman.service;

import com.drissman.domain.model.Vehicle;
import com.drissman.ports.inbound.VehicleUseCase;
import com.drissman.ports.outbound.VehicleRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class VehicleApplicationService implements VehicleUseCase {

    private final VehicleRepositoryPort vehicleRepositoryPort;

    @Override
    public Mono<Vehicle> createVehicle(Vehicle vehicle) {
        if (vehicle.getId() == null) {
            vehicle.setId(UUID.randomUUID());
        }
        vehicle.setCreatedAt(LocalDateTime.now());
        vehicle.setUpdatedAt(LocalDateTime.now());
        if (vehicle.getStatus() == null) {
            vehicle.setStatus(Vehicle.Status.ACTIVE);
        }
        return vehicleRepositoryPort.save(vehicle);
    }

    @Override
    public Mono<Vehicle> updateVehicle(UUID id, Vehicle vehicleData) {
        return vehicleRepositoryPort.findById(id)
                .flatMap(existing -> {
                    if (vehicleData.getBrand() != null) existing.setBrand(vehicleData.getBrand());
                    if (vehicleData.getModel() != null) existing.setModel(vehicleData.getModel());
                    if (vehicleData.getRegistrationNumber() != null) existing.setRegistrationNumber(vehicleData.getRegistrationNumber());
                    if (vehicleData.getTransmission() != null) existing.setTransmission(vehicleData.getTransmission());
                    if (vehicleData.getStatus() != null) existing.setStatus(vehicleData.getStatus());
                    existing.setUpdatedAt(LocalDateTime.now());
                    return vehicleRepositoryPort.save(existing);
                });
    }

    @Override
    public Mono<Vehicle> getVehicle(UUID id) {
        return vehicleRepositoryPort.findById(id);
    }

    @Override
    public Flux<Vehicle> getVehiclesBySchool(UUID schoolId) {
        return vehicleRepositoryPort.findBySchoolId(schoolId);
    }

    @Override
    public Mono<Void> deleteVehicle(UUID id) {
        return vehicleRepositoryPort.deleteById(id);
    }
}
