package com.drissman.adapters.outbound.persistence.mapper;

import com.drissman.adapters.outbound.persistence.entity.LessonRegistrationEntity;
import com.drissman.domain.model.LessonRegistration;

public class LessonRegistrationPersistenceMapper {

    private LessonRegistrationPersistenceMapper() {
        // Utility class
    }

    public static LessonRegistration toDomain(LessonRegistrationEntity entity) {
        if (entity == null) {
            return null;
        }
        return LessonRegistration.builder()
                .id(entity.getId())
                .lessonId(entity.getLessonId())
                .studentId(entity.getStudentId())
                .status(entity.getStatus())
                .attendedAt(entity.getAttendedAt())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static LessonRegistrationEntity toEntity(LessonRegistration domain) {
        if (domain == null) {
            return null;
        }
        return LessonRegistrationEntity.builder()
                .id(domain.getId())
                .lessonId(domain.getLessonId())
                .studentId(domain.getStudentId())
                .status(domain.getStatus())
                .attendedAt(domain.getAttendedAt())
                .notes(domain.getNotes())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
