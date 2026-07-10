package com.drissman.adapters.outbound.persistence;

import com.drissman.adapters.outbound.persistence.mapper.SchoolPersistenceMapper;
import com.drissman.adapters.outbound.persistence.repository.SpringDataSchoolRepository;
import com.drissman.domain.model.School;
import com.drissman.ports.outbound.SchoolRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SchoolPersistenceAdapter implements SchoolRepositoryPort {

    private final SpringDataSchoolRepository springDataSchoolRepository;

    @Override
    public Mono<School> findById(UUID id) {
        return springDataSchoolRepository.findById(id)
                .map(SchoolPersistenceMapper::toDomain);
    }

    @Override
    public Flux<School> findAll() {
        return springDataSchoolRepository.findAll()
                .map(SchoolPersistenceMapper::toDomain);
    }

    @Override
    public Flux<School> findByCity(String city) {
        return springDataSchoolRepository.findByCity(city)
                .map(SchoolPersistenceMapper::toDomain);
    }

    @Override
    public Flux<School> findByCityOrderByRatingDesc(String city) {
        return springDataSchoolRepository.findByCityOrderByRatingDesc(city)
                .map(SchoolPersistenceMapper::toDomain);
    }

    @Override
    public Mono<School> save(School school) {
        return springDataSchoolRepository.save(SchoolPersistenceMapper.toEntity(school))
                .map(SchoolPersistenceMapper::toDomain);
    }
}
