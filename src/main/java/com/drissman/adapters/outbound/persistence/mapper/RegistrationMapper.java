package com.drissman.adapters.outbound.persistence.mapper;

import com.drissman.adapters.outbound.persistence.entity.RegistrationEntity;
import com.drissman.domain.model.Registration;

public class RegistrationMapper {
    
    public static Registration toDomain(RegistrationEntity entity) {
        if (entity == null) return null;
        return Registration.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .schoolId(entity.getSchoolId())
                .formationId(entity.getFormationId())
                .category(entity.getCategory())
                .status(entity.getStatus() != null ? Registration.RegistrationStatus.valueOf(entity.getStatus()) : Registration.RegistrationStatus.PENDING)
                .paymentStatus(entity.getPaymentStatus() != null ? Registration.PaymentStatus.valueOf(entity.getPaymentStatus()) : Registration.PaymentStatus.NOT_REQUIRED)
                .documentsStatus(entity.getDocumentsStatus() != null ? Registration.DocumentsStatus.valueOf(entity.getDocumentsStatus()) : Registration.DocumentsStatus.NOT_REQUIRED)
                .createdAt(entity.getCreatedAt())
                .validatedAt(entity.getValidatedAt())
                .validatedBy(entity.getValidatedBy())
                .remarks(entity.getRemarks())
                .build();
    }
    
    public static RegistrationEntity toEntity(Registration domain) {
        if (domain == null) return null;
        return RegistrationEntity.builder()
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
