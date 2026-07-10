package com.drissman.adapters.outbound.persistence.mapper;

import com.drissman.adapters.outbound.persistence.entity.MonitorEntity;
import com.drissman.domain.model.Monitor;

public class MonitorPersistenceMapper {

    private MonitorPersistenceMapper() {}

    public static Monitor toDomain(MonitorEntity entity) {
        if (entity == null) return null;

        Monitor.MonitorStatus status = null;
        if (entity.getStatus() != null) {
            try {
                status = Monitor.MonitorStatus.valueOf(entity.getStatus());
            } catch (IllegalArgumentException ignored) {}
        }

        return Monitor.builder()
                .id(entity.getId())
                .schoolId(entity.getSchoolId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .phoneNumber(entity.getPhoneNumber())
                .specialties(entity.getSpecialties())
                .avatarUrl(entity.getAvatarUrl())
                .licenseNumber(entity.getLicenseNumber())
                .userId(entity.getUserId())
                .isActive(entity.getIsActive())
                .status(status)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static MonitorEntity toEntity(Monitor domain) {
        if (domain == null) return null;

        return MonitorEntity.builder()
                .id(domain.getId())
                .schoolId(domain.getSchoolId())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .phoneNumber(domain.getPhoneNumber())
                .specialties(domain.getSpecialties())
                .avatarUrl(domain.getAvatarUrl())
                .licenseNumber(domain.getLicenseNumber())
                .userId(domain.getUserId())
                .isActive(domain.getIsActive() != null ? domain.getIsActive() : true)
                .status(domain.getStatus() != null ? domain.getStatus().name() : null)
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
