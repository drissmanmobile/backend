package com.drissman.adapters.inbound.rest.mapper;

import com.drissman.adapters.inbound.rest.dto.MonitorDto;
import com.drissman.domain.model.Monitor;

public class MonitorRestMapper {

    private MonitorRestMapper() {
        // Utility class
    }

    public static MonitorDto toDto(Monitor domain) {
        if (domain == null) {
            return null;
        }
        return MonitorDto.builder()
                .id(domain.getId())
                .schoolId(domain.getSchoolId())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .licenseNumber(domain.getLicenseNumber())
                .phoneNumber(domain.getPhoneNumber())
                .userId(domain.getUserId())
                .status(domain.getStatus())
                .build();
    }
}
