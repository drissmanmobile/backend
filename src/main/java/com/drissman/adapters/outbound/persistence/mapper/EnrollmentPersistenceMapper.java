package com.drissman.adapters.outbound.persistence.mapper;

import com.drissman.adapters.outbound.persistence.entity.EnrollmentEntity;
import com.drissman.domain.model.Enrollment;

public class EnrollmentPersistenceMapper {

    private EnrollmentPersistenceMapper() {}

    public static Enrollment toDomain(EnrollmentEntity entity) {
        if (entity == null) return null;
        
        Enrollment.EnrollmentStatus status = null;
        if (entity.getStatus() != null) {
            try {
                status = Enrollment.EnrollmentStatus.valueOf(entity.getStatus());
            } catch (IllegalArgumentException ignored) {}
        }

        return Enrollment.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .schoolId(entity.getSchoolId())
                .offerId(entity.getOfferId())
                .trainingPeriodId(entity.getTrainingPeriodId())
                .enrolledAt(entity.getEnrolledAt())
                .status(status)
                .hoursPurchased(entity.getHoursPurchased())
                .hoursConsumed(entity.getHoursConsumed())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static EnrollmentEntity toEntity(Enrollment domain) {
        if (domain == null) return null;

        return EnrollmentEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .schoolId(domain.getSchoolId())
                .offerId(domain.getOfferId())
                .trainingPeriodId(domain.getTrainingPeriodId())
                .enrolledAt(domain.getEnrolledAt())
                .status(domain.getStatus() != null ? domain.getStatus().name() : null)
                .hoursPurchased(domain.getHoursPurchased())
                .hoursConsumed(domain.getHoursConsumed() != null ? domain.getHoursConsumed() : 0)
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
