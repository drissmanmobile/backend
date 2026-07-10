package com.drissman.service;

import com.drissman.domain.model.Enrollment;
import com.drissman.domain.model.Review;
import com.drissman.domain.model.ReviewDetails;
import com.drissman.ports.inbound.ReviewUseCase;
import com.drissman.ports.outbound.EnrollmentRepositoryPort;
import com.drissman.ports.outbound.ReviewRepositoryPort;
import com.drissman.ports.outbound.SchoolRepositoryPort;
import com.drissman.ports.outbound.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class ReviewApplicationService implements ReviewUseCase {

    private final ReviewRepositoryPort reviewRepository;
    private final UserRepositoryPort userRepository;
    private final SchoolRepositoryPort schoolRepository;
    private final EnrollmentRepositoryPort enrollmentRepository;

    @Override
    @Transactional
    public Mono<ReviewDetails> create(UUID userId, UUID schoolId, Integer rating, String comment) {
        log.info("Creating review for user {} and school {}. Rating: {}", userId, schoolId, rating);

        return enrollmentRepository.findByUserId(userId)
                .collectList()
                .flatMap(enrollments -> {
                    log.info("Found {} enrollments for user {}", enrollments.size(), userId);

                    boolean hasValidEnrollment = enrollments.stream()
                            .anyMatch(enrollment -> enrollment.getSchoolId().equals(schoolId) &&
                                    (enrollment.getStatus() == Enrollment.EnrollmentStatus.ACTIVE
                                            || enrollment.getStatus() == Enrollment.EnrollmentStatus.COMPLETED));

                    if (!hasValidEnrollment) {
                        log.warn("Review rejected: No active/completed enrollment found for user {} and school {}",
                                userId, schoolId);
                        return Mono.error(new RuntimeException(
                                "Vous devez avoir une inscription active pour laisser un avis."));
                    }

                    return reviewRepository.findByUserIdAndSchoolId(userId, schoolId)
                            .flatMap(existing -> {
                                log.warn("Review rejected: User {} already reviewed school {}",
                                        userId, schoolId);
                                return Mono.<ReviewDetails>error(new RuntimeException(
                                        "Vous avez déjà laissé un avis pour cette auto-école."));
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                log.info("Saving new review for user {} and school {}",
                                        userId, schoolId);
                                Review review = Review.builder()
                                        .userId(userId)
                                        .schoolId(schoolId)
                                        .rating(rating)
                                        .comment(comment)
                                        .verified(false)
                                        .createdAt(LocalDateTime.now())
                                        .build();

                                return reviewRepository.save(review)
                                        .flatMap(saved -> {
                                            log.info("Review saved successfully: {}", saved.getId());
                                            return enrichWithUserName(saved)
                                                    .flatMap(dto -> updateSchoolRating(schoolId)
                                                            .thenReturn(dto));
                                        });
                            }));
                });
    }

    @Override
    public Flux<ReviewDetails> findBySchoolId(UUID schoolId) {
        return reviewRepository.findBySchoolId(schoolId)
                .flatMap(this::enrichWithUserName);
    }

    @Override
    @Transactional
    public Mono<ReviewDetails> verifyReview(UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .flatMap(review -> {
                    review.setVerified(true);
                    return reviewRepository.save(review);
                })
                .flatMap(this::enrichWithUserName);
    }

    @Override
    @Transactional
    public Mono<Void> delete(UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .flatMap(review -> reviewRepository.deleteById(reviewId)
                        .then(updateSchoolRating(review.getSchoolId())));
    }

    private Mono<ReviewDetails> enrichWithUserName(Review review) {
        return userRepository.findById(review.getUserId())
                .map(user -> ReviewDetails.builder()
                        .id(review.getId())
                        .userId(review.getUserId())
                        .userName(user.getFirstName() + " " +
                                (user.getLastName() != null && !user.getLastName().isEmpty()
                                        ? user.getLastName().charAt(0) + "." : ""))
                        .schoolId(review.getSchoolId())
                        .rating(review.getRating())
                        .comment(review.getComment())
                        .verified(review.getVerified())
                        .createdAt(review.getCreatedAt())
                        .build())
                .switchIfEmpty(Mono.just(ReviewDetails.builder()
                        .id(review.getId())
                        .userId(review.getUserId())
                        .userName("Anonyme")
                        .schoolId(review.getSchoolId())
                        .rating(review.getRating())
                        .comment(review.getComment())
                        .verified(review.getVerified())
                        .createdAt(review.getCreatedAt())
                        .build()));
    }

    private Mono<Void> updateSchoolRating(UUID schoolId) {
        return reviewRepository.getAverageRatingBySchoolId(schoolId)
                .flatMap(avgRating -> schoolRepository.findById(schoolId)
                        .flatMap(school -> {
                            school.setRating(BigDecimal.valueOf(avgRating));
                            return schoolRepository.save(school);
                        }))
                .then();
    }
}
