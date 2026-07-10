package com.drissman.ports.outbound;

import com.drissman.domain.model.Review;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReviewRepositoryPort {
    Flux<Review> findBySchoolId(UUID schoolId);
    Flux<Review> findByUserId(UUID userId);
    Mono<Review> findByUserIdAndSchoolId(UUID userId, UUID schoolId);
    Mono<Double> getAverageRatingBySchoolId(UUID schoolId);
    Mono<Long> countBySchoolId(UUID schoolId);
    Mono<Review> findById(UUID id);
    Mono<Review> save(Review review);
    Mono<Void> deleteById(UUID id);
}
