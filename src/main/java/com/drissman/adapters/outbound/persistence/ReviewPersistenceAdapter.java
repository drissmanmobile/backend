package com.drissman.adapters.outbound.persistence;

import com.drissman.adapters.outbound.persistence.mapper.ReviewPersistenceMapper;
import com.drissman.adapters.outbound.persistence.repository.SpringDataReviewRepository;
import com.drissman.domain.model.Review;
import com.drissman.ports.outbound.ReviewRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReviewPersistenceAdapter implements ReviewRepositoryPort {

    private final SpringDataReviewRepository repository;

    @Override
    public Flux<Review> findBySchoolId(UUID schoolId) {
        return repository.findBySchoolId(schoolId)
                .map(ReviewPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Review> findByUserId(UUID userId) {
        return repository.findByUserId(userId)
                .map(ReviewPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Review> findByUserIdAndSchoolId(UUID userId, UUID schoolId) {
        return repository.findByUserIdAndSchoolId(userId, schoolId)
                .map(ReviewPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Double> getAverageRatingBySchoolId(UUID schoolId) {
        return repository.getAverageRatingBySchoolId(schoolId);
    }

    @Override
    public Mono<Long> countBySchoolId(UUID schoolId) {
        return repository.countBySchoolId(schoolId);
    }

    @Override
    public Mono<Review> findById(UUID id) {
        return repository.findById(id)
                .map(ReviewPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Review> save(Review review) {
        return repository.save(ReviewPersistenceMapper.toEntity(review))
                .map(ReviewPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }
}
