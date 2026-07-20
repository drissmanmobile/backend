package com.drissman.adapters.inbound.rest.mapper;

import com.drissman.adapters.inbound.rest.dto.VehicleDto;
import com.drissman.domain.model.Vehicle;

public class VehicleRestMapper {

    public static VehicleDto toDto(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }

        return VehicleDto.builder()
                .id(vehicle.getId())
                .schoolId(vehicle.getSchoolId())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .registrationNumber(vehicle.getRegistrationNumber())
                .transmission(vehicle.getTransmission() != null ? vehicle.getTransmission().name() : null)
                .status(vehicle.getStatus() != null ? vehicle.getStatus().name() : null)
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }

    public static Vehicle toDomain(VehicleDto dto) {
        if (dto == null) {
            return null;
        }

        return Vehicle.builder()
                .id(dto.getId())
                .schoolId(dto.getSchoolId())
                .brand(dto.getBrand())
                .model(dto.getModel())
                .registrationNumber(dto.getRegistrationNumber())
                .transmission(dto.getTransmission() != null ? Vehicle.Transmission.valueOf(dto.getTransmission()) : null)
                .status(dto.getStatus() != null ? Vehicle.Status.valueOf(dto.getStatus()) : null)
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }
}
