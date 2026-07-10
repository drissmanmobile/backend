package com.drissman.ports.inbound;

import com.drissman.domain.model.School;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SchoolUseCase {
    Flux<School> findAll(String city);
    Flux<School> findNearby(double lat, double lng, double radiusKm);
    Mono<School> findById(UUID id);
    Mono<School> update(UUID id, String name, String description, String address, String city, String region, String phone, String email, String website, String imageUrl);
    Mono<School> save(School school);
}
