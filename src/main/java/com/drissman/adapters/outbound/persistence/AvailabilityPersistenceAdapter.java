package com.drissman.adapters.outbound.persistence;

import com.drissman.adapters.outbound.persistence.mapper.AvailabilityPersistenceMapper;
import com.drissman.adapters.outbound.persistence.repository.SpringDataAvailabilityRepository;
import com.drissman.domain.model.Availability;
import com.drissman.ports.outbound.AvailabilityRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AvailabilityPersistenceAdapter implements AvailabilityRepositoryPort {

    private final SpringDataAvailabilityRepository repository;

    @Override
    public Flux<Availability> findBySchoolId(UUID schoolId) {
        return repository.findBySchoolId(schoolId)
                .map(AvailabilityPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Availability> findBySchoolIdAndDayOfWeek(UUID schoolId, Integer dayOfWeek) {
        return repository.findBySchoolIdAndDayOfWeek(schoolId, dayOfWeek)
                .map(AvailabilityPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Availability> findById(UUID id) {
        return repository.findById(id)
                .map(AvailabilityPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Availability> save(Availability availability) {
        return repository.save(AvailabilityPersistenceMapper.toEntity(availability))
                .map(AvailabilityPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }
}
