package com.drissman.adapters.outbound.persistence;

import com.drissman.adapters.outbound.persistence.mapper.TrainingPeriodPersistenceMapper;
import com.drissman.adapters.outbound.persistence.repository.SpringDataTrainingPeriodRepository;
import com.drissman.domain.model.TrainingPeriod;
import com.drissman.ports.outbound.TrainingPeriodRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TrainingPeriodPersistenceAdapter implements TrainingPeriodRepositoryPort {

    private final SpringDataTrainingPeriodRepository repository;

    @Override
    public Flux<TrainingPeriod> findBySchoolId(UUID schoolId) {
        return repository.findBySchoolId(schoolId)
                .map(TrainingPeriodPersistenceMapper::toDomain);
    }

    @Override
    public Mono<TrainingPeriod> findById(UUID id) {
        return repository.findById(id)
                .map(TrainingPeriodPersistenceMapper::toDomain);
    }

    @Override
    public Mono<TrainingPeriod> save(TrainingPeriod period) {
        return repository.save(TrainingPeriodPersistenceMapper.toEntity(period))
                .map(TrainingPeriodPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Void> delete(TrainingPeriod period) {
        return repository.delete(TrainingPeriodPersistenceMapper.toEntity(period));
    }
}
