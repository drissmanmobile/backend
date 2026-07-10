package com.drissman.adapters.outbound.persistence.mapper;

import com.drissman.adapters.outbound.persistence.entity.LessonEntity;
import com.drissman.domain.model.Lesson;

public class LessonPersistenceMapper {

    private LessonPersistenceMapper() {
        // Utility class
    }

    public static Lesson toDomain(LessonEntity entity) {
        if (entity == null) {
            return null;
        }
        return Lesson.builder()
                .id(entity.getId())
                .schoolId(entity.getSchoolId())
                .monitorId(entity.getMonitorId())
                .date(entity.getDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .topic(entity.getTopic())
                .lessonType(entity.getLessonType())
                .moduleId(entity.getModuleId())
                .trainingPeriodId(entity.getTrainingPeriodId())
                .description(entity.getDescription())
                .roomId(entity.getRoomId())
                .capacity(entity.getCapacity())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static LessonEntity toEntity(Lesson domain) {
        if (domain == null) {
            return null;
        }
        return LessonEntity.builder()
                .id(domain.getId())
                .schoolId(domain.getSchoolId())
                .monitorId(domain.getMonitorId())
                .date(domain.getDate())
                .startTime(domain.getStartTime())
                .endTime(domain.getEndTime())
                .topic(domain.getTopic())
                .lessonType(domain.getLessonType())
                .moduleId(domain.getModuleId())
                .trainingPeriodId(domain.getTrainingPeriodId())
                .description(domain.getDescription())
                .roomId(domain.getRoomId())
                .capacity(domain.getCapacity())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
