package com.drissman.ports.inbound;

import com.drissman.domain.model.Enrollment;
import com.drissman.domain.model.Offer;
import com.drissman.domain.model.Session;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public interface SessionUseCase {
    Mono<Session> scheduleSession(UUID schoolId, UUID enrollmentId, UUID offerId, UUID monitorId, UUID vehicleId, UUID moduleId, UUID lessonId, LocalDate date, LocalTime startTime, LocalTime endTime, String meetingPoint);
    Flux<Session> getSessionsForEnrollment(UUID schoolId, UUID enrollmentId);
    Flux<Session> getSessionsForMonitor(UUID schoolId, UUID monitorId);
    Mono<Session> getSessionById(UUID id);
    Flux<Offer> getAvailableOffersForDate(UUID schoolId, LocalDate date);
    Flux<Enrollment> getAvailableEnrollments(UUID schoolId, UUID offerId, LocalDate date);
    Mono<Void> cancelSession(UUID schoolId, UUID sessionId);
    Mono<Session> completeSession(UUID schoolId, UUID sessionId, String pedagogicalNotes);
    Flux<Session> getMonitorSessionsByUserId(UUID userId);
    Mono<Session> completeSessionByMonitor(UUID userId, UUID sessionId, String pedagogicalNotes);
    Flux<Enrollment> getStudentsForMonitor(UUID userId);
    Flux<Session> getSessionsForStudent(UUID userId);
}
