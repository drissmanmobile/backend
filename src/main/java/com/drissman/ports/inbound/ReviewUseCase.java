package com.drissman.ports.inbound;

import com.drissman.domain.model.ReviewDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReviewUseCase {
    Mono<ReviewDetails> create(UUID userId, UUID schoolId, Integer rating, String comment);
    Flux<ReviewDetails> findBySchoolId(UUID schoolId);
    Mono<ReviewDetails> verifyReview(UUID reviewId);
    Mono<Void> delete(UUID reviewId);
}
