package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.ReviewEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface SpringDataReviewRepository extends ReactiveCrudRepository<ReviewEntity, UUID> {
    Flux<ReviewEntity> findBySchoolId(UUID schoolId);

    Flux<ReviewEntity> findByUserId(UUID userId);

    Mono<ReviewEntity> findByUserIdAndSchoolId(UUID userId, UUID schoolId);

    @Query("SELECT AVG(rating) FROM reviews WHERE school_id = :schoolId")
    Mono<Double> getAverageRatingBySchoolId(UUID schoolId);

    @Query("SELECT COUNT(*) FROM reviews WHERE school_id = :schoolId")
    Mono<Long> countBySchoolId(UUID schoolId);
}
