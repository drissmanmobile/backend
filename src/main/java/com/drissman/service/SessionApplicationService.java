package com.drissman.service;

import com.drissman.domain.model.TrainingPeriod;
import com.drissman.domain.model.Enrollment;
import com.drissman.domain.model.Offer;
import com.drissman.domain.model.Session;
import com.drissman.ports.outbound.TrainingPeriodRepositoryPort;
import com.drissman.ports.inbound.SessionUseCase;
import com.drissman.ports.outbound.EnrollmentRepositoryPort;
import com.drissman.ports.outbound.MonitorRepositoryPort;
import com.drissman.ports.outbound.OfferRepositoryPort;
import com.drissman.ports.outbound.SessionRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@RequiredArgsConstructor
public class SessionApplicationService implements SessionUseCase {

    private final SessionRepositoryPort sessionRepositoryPort;
    private final EnrollmentRepositoryPort enrollmentRepositoryPort;
    private final MonitorRepositoryPort monitorRepositoryPort;
    private final OfferRepositoryPort offerRepositoryPort;
    private final TrainingPeriodRepositoryPort trainingPeriodRepository;

    @Override
    public Mono<Session> scheduleSession(UUID schoolId, UUID enrollmentId, UUID offerId, UUID monitorId, UUID vehicleId, UUID moduleId, UUID lessonId, LocalDate date, LocalTime startTime, LocalTime endTime, String meetingPoint) {
        return enrollmentRepositoryPort.findById(enrollmentId)
                .filter(enrollment -> schoolId.equals(enrollment.getSchoolId()))
                .switchIfEmpty(Mono.error(new RuntimeException("Inscription introuvable pour cette auto-ecole")))
                .flatMap(enrollment -> validateMonitorSchool(schoolId, monitorId)
                        .then(Mono.defer(() -> createSession(enrollment, offerId, monitorId, vehicleId, moduleId, lessonId, date, startTime, endTime, meetingPoint))));
    }

    @Override
    public Flux<Session> getSessionsForEnrollment(UUID schoolId, UUID enrollmentId) {
        return enrollmentRepositoryPort.findById(enrollmentId)
                .filter(enrollment -> schoolId.equals(enrollment.getSchoolId()))
                .switchIfEmpty(Mono.error(new RuntimeException("Inscription introuvable pour cette auto-ecole")))
                .thenMany(sessionRepositoryPort.findByEnrollmentId(enrollmentId));
    }

    @Override
    public Flux<Session> getSessionsForMonitor(UUID schoolId, UUID monitorId) {
        return monitorRepositoryPort.findById(monitorId)
                .filter(monitor -> schoolId.equals(monitor.getSchoolId()))
                .switchIfEmpty(Mono.error(new RuntimeException("Moniteur introuvable pour cette auto-ecole")))
                .thenMany(sessionRepositoryPort.findByMonitorId(monitorId));
    }

    @Override
    public Mono<Session> getSessionById(UUID id) {
        return sessionRepositoryPort.findById(id);
    }

    @Override
    public Flux<Offer> getAvailableOffersForDate(UUID schoolId, LocalDate date) {
        return enrollmentRepositoryPort.findBySchoolId(schoolId)
                .filter(enrollment -> enrollment.getStatus() == Enrollment.EnrollmentStatus.ACTIVE
                        || enrollment.getStatus() == Enrollment.EnrollmentStatus.PENDING)
                .flatMap(enrollment -> isEnrollmentValidForDate(enrollment, date)
                        .filter(Boolean::booleanValue)
                        .map(valid -> enrollment.getOfferId()))
                .distinct()
                .flatMap(offerRepositoryPort::findById);
    }

    @Override
    public Flux<Enrollment> getAvailableEnrollments(UUID schoolId, UUID offerId, LocalDate date) {
        return enrollmentRepositoryPort.findBySchoolId(schoolId)
                .filter(enrollment -> offerId.equals(enrollment.getOfferId()))
                .filter(enrollment -> enrollment.getStatus() == Enrollment.EnrollmentStatus.ACTIVE
                        || enrollment.getStatus() == Enrollment.EnrollmentStatus.PENDING)
                .flatMap(enrollment -> isEnrollmentValidForDate(enrollment, date)
                        .filter(Boolean::booleanValue)
                        .map(valid -> enrollment));
    }

    @Override
    public Mono<Void> cancelSession(UUID schoolId, UUID sessionId) {
        return sessionRepositoryPort.findById(sessionId)
                .switchIfEmpty(Mono.error(new RuntimeException("Session introuvable")))
                .flatMap(session -> ensureSessionBelongsToSchool(session, schoolId)
                        .then(Mono.defer(() -> {
                            session.setStatus(Session.SessionStatus.CANCELLED);
                            return sessionRepositoryPort.save(session).then();
                        })));
    }

    @Override
    public Mono<Session> completeSession(UUID schoolId, UUID sessionId, String pedagogicalNotes) {
        return sessionRepositoryPort.findById(sessionId)
                .switchIfEmpty(Mono.error(new RuntimeException("Session introuvable")))
                .flatMap(session -> ensureSessionBelongsToSchool(session, schoolId)
                        .then(enrollmentRepositoryPort.findById(session.getEnrollmentId())
                                .flatMap(enrollment -> {
                                    session.setStatus(Session.SessionStatus.COMPLETED);
                                    if (pedagogicalNotes != null) {
                                        session.setPedagogicalNotes(pedagogicalNotes);
                                    }

                                    enrollment.setHoursConsumed(enrollment.getHoursConsumed() + session.getDurationHours());

                                    return enrollmentRepositoryPort.save(enrollment)
                                            .then(sessionRepositoryPort.save(session));
                                })));
    }

    @Override
    public Flux<Session> getMonitorSessionsByUserId(UUID userId) {
        return monitorRepositoryPort.findByUserId(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("Profil moniteur introuvable")))
                .flatMapMany(monitor -> sessionRepositoryPort.findByMonitorId(monitor.getId()));
    }

    @Override
    public Mono<Session> completeSessionByMonitor(UUID userId, UUID sessionId, String pedagogicalNotes) {
        return monitorRepositoryPort.findByUserId(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("Profil moniteur introuvable")))
                .flatMap(monitor -> sessionRepositoryPort.findById(sessionId)
                        .switchIfEmpty(Mono.error(new RuntimeException("Session introuvable")))
                        .filter(session -> monitor.getId().equals(session.getMonitorId()))
                        .switchIfEmpty(Mono.error(new RuntimeException("Session non assignee a ce moniteur")))
                        .flatMap(session -> completeSession(monitor.getSchoolId(), session.getId(), pedagogicalNotes)));
    }

    @Override
    public Flux<Enrollment> getStudentsForMonitor(UUID userId) {
        return monitorRepositoryPort.findByUserId(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("Profil moniteur introuvable")))
                .flatMapMany(monitor -> sessionRepositoryPort.findByMonitorId(monitor.getId())
                        .map(Session::getEnrollmentId)
                        .distinct()
                        .flatMap(enrollmentRepositoryPort::findById));
    }

    @Override
    public Flux<Session> getSessionsForStudent(UUID userId) {
        return enrollmentRepositoryPort.findByUserId(userId)
                .flatMap(enrollment -> sessionRepositoryPort.findByEnrollmentId(enrollment.getId()));
    }

    private Mono<Void> validateMonitorSchool(UUID schoolId, UUID monitorId) {
        if (monitorId == null) {
            return Mono.empty();
        }
        return monitorRepositoryPort.findById(monitorId)
                .filter(monitor -> schoolId.equals(monitor.getSchoolId()))
                .switchIfEmpty(Mono.error(new RuntimeException("Moniteur invalide pour cette auto-ecole")))
                .then();
    }

    private Mono<Void> ensureSessionBelongsToSchool(Session session, UUID schoolId) {
        return enrollmentRepositoryPort.findById(session.getEnrollmentId())
                .filter(enrollment -> schoolId.equals(enrollment.getSchoolId()))
                .switchIfEmpty(Mono.error(new RuntimeException("Session hors perimetre auto-ecole")))
                .then();
    }

    private Mono<Session> createSession(Enrollment enrollment, UUID offerId, UUID monitorId, UUID vehicleId, UUID moduleId, UUID lessonId, LocalDate date, LocalTime startTime, LocalTime endTime, String meetingPoint) {
        Session session = Session.builder()
                .enrollmentId(enrollment.getId())
                .offerId(offerId)
                .monitorId(monitorId)
                .vehicleId(vehicleId)
                .moduleId(moduleId)
                .lessonId(lessonId)
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .meetingPoint(meetingPoint)
                .status(Session.SessionStatus.SCHEDULED)
                .createdAt(LocalDateTime.now())
                .build();

        int duration = session.getDurationHours();
        if (enrollment.getRemainingHours() < duration) {
            return Mono.error(new RuntimeException("Pas assez d'heures restantes sur l'inscription"));
        }

        return sessionRepositoryPort.save(session);
    }

    private boolean isDateInPeriod(TrainingPeriod period, LocalDate date) {
        if (period.getStartDate() == null || period.getEndDate() == null) {
            return false;
        }
        return !date.isBefore(period.getStartDate()) && !date.isAfter(period.getEndDate());
    }

    private Mono<Boolean> isEnrollmentValidForDate(Enrollment enrollment, LocalDate date) {
        if (enrollment.getTrainingPeriodId() == null) {
            return Mono.just(true);
        }

        return trainingPeriodRepository.findById(enrollment.getTrainingPeriodId())
                .map(period -> isDateInPeriod(period, date))
                .defaultIfEmpty(false);
    }
}
