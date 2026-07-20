package com.drissman.adapters.outbound.persistence.mapper;

import com.drissman.adapters.outbound.persistence.entity.SessionEntity;
import com.drissman.domain.model.Session;

public class SessionPersistenceMapper {

    private SessionPersistenceMapper() {}

    public static Session toDomain(SessionEntity entity) {
        if (entity == null) return null;
        
        Session.SessionStatus status = null;
        if (entity.getStatus() != null) {
            try {
                status = Session.SessionStatus.valueOf(entity.getStatus());
            } catch (IllegalArgumentException ignored) {}
        }

        return Session.builder()
                .id(entity.getId())
                .enrollmentId(entity.getEnrollmentId())
                .offerId(entity.getOfferId())
                .monitorId(entity.getMonitorId())
                .vehicleId(entity.getVehicleId())
                .moduleId(entity.getModuleId())
                .lessonId(entity.getLessonId())
                .date(entity.getDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .status(status)
                .meetingPoint(entity.getMeetingPoint())
                .pedagogicalNotes(entity.getPedagogicalNotes())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static SessionEntity toEntity(Session domain) {
        if (domain == null) return null;

        return SessionEntity.builder()
                .id(domain.getId())
                .enrollmentId(domain.getEnrollmentId())
                .offerId(domain.getOfferId())
                .monitorId(domain.getMonitorId())
                .vehicleId(domain.getVehicleId())
                .moduleId(domain.getModuleId())
                .lessonId(domain.getLessonId())
                .date(domain.getDate())
                .startTime(domain.getStartTime())
                .endTime(domain.getEndTime())
                .status(domain.getStatus() != null ? domain.getStatus().name() : null)
                .meetingPoint(domain.getMeetingPoint())
                .pedagogicalNotes(domain.getPedagogicalNotes())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
