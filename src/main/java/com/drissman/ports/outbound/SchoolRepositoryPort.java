package com.drissman.ports.outbound;

import com.drissman.domain.model.School;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SchoolRepositoryPort {
    Mono<School> findById(UUID id);
    Flux<School> findAll();
    Flux<School> findByCity(String city);
    Flux<School> findByCityOrderByRatingDesc(String city);
    Mono<School> save(School school);
}
