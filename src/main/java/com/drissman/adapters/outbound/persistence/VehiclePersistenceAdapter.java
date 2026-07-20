package com.drissman.adapters.outbound.persistence;

import com.drissman.adapters.outbound.persistence.mapper.VehiclePersistenceMapper;
import com.drissman.adapters.outbound.persistence.repository.SpringDataVehicleRepository;
import com.drissman.domain.model.Vehicle;
import com.drissman.ports.outbound.VehicleRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VehiclePersistenceAdapter implements VehicleRepositoryPort {

    private final SpringDataVehicleRepository repository;

    @Override
    public Mono<Vehicle> save(Vehicle vehicle) {
        return repository.save(VehiclePersistenceMapper.toEntity(vehicle))
                .map(VehiclePersistenceMapper::toDomain);
    }

    @Override
    public Mono<Vehicle> findById(UUID id) {
        return repository.findById(id)
                .map(VehiclePersistenceMapper::toDomain);
    }

    @Override
    public Flux<Vehicle> findBySchoolId(UUID schoolId) {
        return repository.findBySchoolId(schoolId)
                .map(VehiclePersistenceMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }
}
