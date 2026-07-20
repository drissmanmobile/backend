package com.drissman.adapters.outbound.persistence.mapper;

import com.drissman.adapters.outbound.persistence.entity.VehicleEntity;
import com.drissman.domain.model.Vehicle;

public class VehiclePersistenceMapper {

    public static Vehicle toDomain(VehicleEntity entity) {
        if (entity == null) {
            return null;
        }

        return Vehicle.builder()
                .id(entity.getId())
                .schoolId(entity.getSchoolId())
                .brand(entity.getBrand())
                .model(entity.getModel())
                .registrationNumber(entity.getRegistrationNumber())
                .transmission(entity.getTransmission() != null ? Vehicle.Transmission.valueOf(entity.getTransmission()) : null)
                .status(entity.getStatus() != null ? Vehicle.Status.valueOf(entity.getStatus()) : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static VehicleEntity toEntity(Vehicle domain) {
        if (domain == null) {
            return null;
        }

        return VehicleEntity.builder()
                .id(domain.getId())
                .schoolId(domain.getSchoolId())
                .brand(domain.getBrand())
                .model(domain.getModel())
                .registrationNumber(domain.getRegistrationNumber())
                .transmission(domain.getTransmission() != null ? domain.getTransmission().name() : null)
                .status(domain.getStatus() != null ? domain.getStatus().name() : null)
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
