package com.drissman.adapters.inbound.rest.mapper;

import com.drissman.adapters.inbound.rest.dto.RegistrationDto;
import com.drissman.domain.model.Registration;

public class RegistrationRestMapper {
    public static RegistrationDto toDto(Registration domain) {
        if (domain == null) return null;
        return RegistrationDto.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .schoolId(domain.getSchoolId())
                .formationId(domain.getFormationId())
                .category(domain.getCategory())
                .status(domain.getStatus() != null ? domain.getStatus().name() : null)
                .paymentStatus(domain.getPaymentStatus() != null ? domain.getPaymentStatus().name() : null)
                .documentsStatus(domain.getDocumentsStatus() != null ? domain.getDocumentsStatus().name() : null)
                .createdAt(domain.getCreatedAt())
                .validatedAt(domain.getValidatedAt())
                .validatedBy(domain.getValidatedBy())
                .remarks(domain.getRemarks())
                .build();
    }
}
