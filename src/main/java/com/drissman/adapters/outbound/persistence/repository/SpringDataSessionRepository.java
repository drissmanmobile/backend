package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.SessionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.UUID;

public interface SpringDataSessionRepository extends ReactiveCrudRepository<SessionEntity, UUID> {
    Flux<SessionEntity> findByEnrollmentId(UUID enrollmentId);

    Flux<SessionEntity> findByMonitorId(UUID monitorId);

    Flux<SessionEntity> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @org.springframework.data.r2dbc.repository.Query("SELECT s.* FROM sessions s JOIN enrollments e ON s.enrollment_id = e.id WHERE e.school_id = :schoolId")
    Flux<SessionEntity> findBySchoolId(UUID schoolId);
}
