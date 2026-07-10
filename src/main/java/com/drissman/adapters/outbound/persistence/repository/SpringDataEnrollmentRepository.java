package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.EnrollmentEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SpringDataEnrollmentRepository extends ReactiveCrudRepository<EnrollmentEntity, UUID> {

    Flux<EnrollmentEntity> findByUserId(UUID userId);

    Flux<EnrollmentEntity> findBySchoolId(UUID schoolId);

    Flux<EnrollmentEntity> findByOfferId(UUID offerId);

    Flux<EnrollmentEntity> findByUserIdAndStatus(UUID userId, String status);

    Mono<EnrollmentEntity> findByUserIdAndOfferId(UUID userId, UUID offerId);

    Mono<Boolean> existsByUserIdAndOfferIdAndStatus(UUID userId, UUID offerId, String status);

    Flux<EnrollmentEntity> findByTrainingPeriodId(UUID trainingPeriodId);

    Mono<Long> countByTrainingPeriodId(UUID trainingPeriodId);
}
