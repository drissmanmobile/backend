package com.drissman.ports.inbound;

import com.drissman.domain.model.Enrollment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EnrollmentUseCase {
    Mono<Enrollment> createEnrollment(UUID userId, UUID offerId);
    Flux<Enrollment> getEnrollmentsForStudent(UUID userId);
    Flux<Enrollment> getEnrollmentsForSchool(UUID schoolId);
    Mono<Enrollment> updateEnrollmentStatus(UUID schoolId, UUID enrollmentId, String statusRaw);
}
