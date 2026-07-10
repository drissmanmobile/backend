package com.drissman.adapters.outbound.persistence.mapper;

import com.drissman.adapters.outbound.persistence.entity.AvailabilityEntity;
import com.drissman.domain.model.Availability;

public class AvailabilityPersistenceMapper {

    private AvailabilityPersistenceMapper() {
        // Utility class
    }

    public static Availability toDomain(AvailabilityEntity entity) {
        if (entity == null) {
            return null;
        }
        return Availability.builder()
                .id(entity.getId())
                .schoolId(entity.getSchoolId())
                .dayOfWeek(entity.getDayOfWeek())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .maxBookings(entity.getMaxBookings())
                .build();
    }

    public static AvailabilityEntity toEntity(Availability domain) {
        if (domain == null) {
            return null;
        }
        return AvailabilityEntity.builder()
                .id(domain.getId())
                .schoolId(domain.getSchoolId())
                .dayOfWeek(domain.getDayOfWeek())
                .startTime(domain.getStartTime())
                .endTime(domain.getEndTime())
                .maxBookings(domain.getMaxBookings())
                .build();
    }
}
