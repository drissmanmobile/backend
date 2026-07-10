package com.drissman.ports.outbound;

import com.drissman.domain.model.Enrollment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EnrollmentRepositoryPort {
    Mono<Enrollment> findById(UUID id);
    Flux<Enrollment> findByUserId(UUID userId);
    Flux<Enrollment> findBySchoolId(UUID schoolId);
    Flux<Enrollment> findByOfferId(UUID offerId);
    Flux<Enrollment> findByUserIdAndStatus(UUID userId, Enrollment.EnrollmentStatus status);
    Mono<Enrollment> findByUserIdAndOfferId(UUID userId, UUID offerId);
    Mono<Boolean> existsByUserIdAndOfferIdAndStatus(UUID userId, UUID offerId, Enrollment.EnrollmentStatus status);
    Flux<Enrollment> findByTrainingPeriodId(UUID trainingPeriodId);
    Mono<Long> countByTrainingPeriodId(UUID trainingPeriodId);
    Mono<Enrollment> save(Enrollment enrollment);
}
