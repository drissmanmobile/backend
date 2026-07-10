package com.drissman.adapters.outbound.persistence.mapper;

import com.drissman.adapters.outbound.persistence.entity.TrainingPeriodEntity;
import com.drissman.domain.model.TrainingPeriod;

public class TrainingPeriodPersistenceMapper {

    private TrainingPeriodPersistenceMapper() {
        // Utility class
    }

    public static TrainingPeriod toDomain(TrainingPeriodEntity entity) {
        if (entity == null) {
            return null;
        }

        TrainingPeriod.TrainingPeriodStatus status = null;
        if (entity.getStatus() != null) {
            try {
                status = TrainingPeriod.TrainingPeriodStatus.valueOf(entity.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                status = TrainingPeriod.TrainingPeriodStatus.DRAFT;
            }
        }

        return TrainingPeriod.builder()
                .id(entity.getId())
                .schoolId(entity.getSchoolId())
                .offerId(entity.getOfferId())
                .name(entity.getName())
                .description(entity.getDescription())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .maxStudents(entity.getMaxStudents())
                .status(status)
                .enrollmentDeadline(entity.getEnrollmentDeadline())
                .scheduleDescription(entity.getScheduleDescription())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static TrainingPeriodEntity toEntity(TrainingPeriod domain) {
        if (domain == null) {
            return null;
        }
        return TrainingPeriodEntity.builder()
                .id(domain.getId())
                .schoolId(domain.getSchoolId())
                .offerId(domain.getOfferId())
                .name(domain.getName())
                .description(domain.getDescription())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .maxStudents(domain.getMaxStudents())
                .status(domain.getStatus() != null ? domain.getStatus().name() : null)
                .enrollmentDeadline(domain.getEnrollmentDeadline())
                .scheduleDescription(domain.getScheduleDescription())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
