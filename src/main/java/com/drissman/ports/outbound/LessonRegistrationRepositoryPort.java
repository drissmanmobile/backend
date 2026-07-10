package com.drissman.ports.outbound;

import com.drissman.domain.model.LessonRegistration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LessonRegistrationRepositoryPort {
    Flux<LessonRegistration> findByLessonId(UUID lessonId);
    Flux<LessonRegistration> findByStudentId(UUID studentId);
    Mono<Boolean> existsByLessonIdAndStudentId(UUID lessonId, UUID studentId);
    Mono<Long> countByLessonId(UUID lessonId);
    Mono<Void> deleteByLessonIdAndStudentId(UUID lessonId, UUID studentId);
    Mono<LessonRegistration> findById(UUID id);
    Mono<LessonRegistration> save(LessonRegistration lessonRegistration);
    Mono<Void> deleteById(UUID id);
}
