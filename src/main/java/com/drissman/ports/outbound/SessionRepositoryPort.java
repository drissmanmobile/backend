package com.drissman.ports.outbound;

import com.drissman.domain.model.Session;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

public interface SessionRepositoryPort {
    Mono<Session> findById(UUID id);
    Flux<Session> findByEnrollmentId(UUID enrollmentId);
    Flux<Session> findByMonitorId(UUID monitorId);
    Flux<Session> findByDateBetween(LocalDate startDate, LocalDate endDate);
    Flux<Session> findBySchoolId(UUID schoolId);
    Mono<Session> save(Session session);
}
