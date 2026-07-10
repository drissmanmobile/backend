package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.LessonRegistrationEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface SpringDataLessonRegistrationRepository extends R2dbcRepository<LessonRegistrationEntity, UUID> {
    Flux<LessonRegistrationEntity> findByLessonId(UUID lessonId);

    Flux<LessonRegistrationEntity> findByStudentId(UUID studentId);

    Mono<Boolean> existsByLessonIdAndStudentId(UUID lessonId, UUID studentId);

    Mono<Long> countByLessonId(UUID lessonId);

    Mono<Void> deleteByLessonIdAndStudentId(UUID lessonId, UUID studentId);
}
