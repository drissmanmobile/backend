package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.LessonEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface SpringDataLessonRepository extends R2dbcRepository<LessonEntity, UUID> {
    Flux<LessonEntity> findBySchoolId(UUID schoolId);

    Flux<LessonEntity> findBySchoolIdAndDateBetween(UUID schoolId, LocalDate startDate, LocalDate endDate);

    Flux<LessonEntity> findByMonitorId(UUID monitorId);

    Flux<LessonEntity> findByTrainingPeriodId(UUID trainingPeriodId);
}
