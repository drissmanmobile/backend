package com.drissman.ports.outbound;

import com.drissman.domain.model.Lesson;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

public interface LessonRepositoryPort {
    Flux<Lesson> findBySchoolId(UUID schoolId);
    Flux<Lesson> findBySchoolIdAndDateBetween(UUID schoolId, LocalDate startDate, LocalDate endDate);
    Flux<Lesson> findByMonitorId(UUID monitorId);
    Flux<Lesson> findByTrainingPeriodId(UUID trainingPeriodId);
    Mono<Lesson> findById(UUID id);
    Mono<Lesson> save(Lesson lesson);
    Mono<Void> deleteById(UUID id);
}
