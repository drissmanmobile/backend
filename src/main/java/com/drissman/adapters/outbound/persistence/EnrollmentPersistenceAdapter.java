package com.drissman.adapters.outbound.persistence;

import com.drissman.adapters.outbound.persistence.mapper.EnrollmentPersistenceMapper;
import com.drissman.adapters.outbound.persistence.repository.SpringDataEnrollmentRepository;
import com.drissman.domain.model.Enrollment;
import com.drissman.ports.outbound.EnrollmentRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EnrollmentPersistenceAdapter implements EnrollmentRepositoryPort {

    private final SpringDataEnrollmentRepository springDataEnrollmentRepository;

    @Override
    public Mono<Enrollment> findById(UUID id) {
        return springDataEnrollmentRepository.findById(id)
                .map(EnrollmentPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Enrollment> findByUserId(UUID userId) {
        return springDataEnrollmentRepository.findByUserId(userId)
                .map(EnrollmentPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Enrollment> findBySchoolId(UUID schoolId) {
        return springDataEnrollmentRepository.findBySchoolId(schoolId)
                .map(EnrollmentPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Enrollment> findByOfferId(UUID offerId) {
        return springDataEnrollmentRepository.findByOfferId(offerId)
                .map(EnrollmentPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Enrollment> findByUserIdAndStatus(UUID userId, Enrollment.EnrollmentStatus status) {
        return springDataEnrollmentRepository.findByUserIdAndStatus(userId, status != null ? status.name() : null)
                .map(EnrollmentPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Enrollment> findByUserIdAndOfferId(UUID userId, UUID offerId) {
        return springDataEnrollmentRepository.findByUserIdAndOfferId(userId, offerId)
                .map(EnrollmentPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByUserIdAndOfferIdAndStatus(UUID userId, UUID offerId, Enrollment.EnrollmentStatus status) {
        return springDataEnrollmentRepository.existsByUserIdAndOfferIdAndStatus(userId, offerId, status != null ? status.name() : null);
    }

    @Override
    public Flux<Enrollment> findByTrainingPeriodId(UUID trainingPeriodId) {
        return springDataEnrollmentRepository.findByTrainingPeriodId(trainingPeriodId)
                .map(EnrollmentPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Long> countByTrainingPeriodId(UUID trainingPeriodId) {
        return springDataEnrollmentRepository.countByTrainingPeriodId(trainingPeriodId);
    }

    @Override
    public Mono<Enrollment> save(Enrollment enrollment) {
        return springDataEnrollmentRepository.save(EnrollmentPersistenceMapper.toEntity(enrollment))
                .map(EnrollmentPersistenceMapper::toDomain);
    }
}
