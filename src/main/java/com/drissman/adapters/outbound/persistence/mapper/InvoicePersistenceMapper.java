package com.drissman.adapters.outbound.persistence.mapper;

import com.drissman.adapters.outbound.persistence.entity.InvoiceEntity;
import com.drissman.domain.model.Invoice;

public class InvoicePersistenceMapper {

    private InvoicePersistenceMapper() {
        // Utility class
    }

    public static Invoice toDomain(InvoiceEntity entity) {
        if (entity == null) {
            return null;
        }
        return Invoice.builder()
                .id(entity.getId())
                .bookingId(entity.getBookingId())
                .enrollmentId(entity.getEnrollmentId())
                .userId(entity.getUserId())
                .schoolId(entity.getSchoolId())
                .amount(entity.getAmount())
                .status(entity.getStatus())
                .paymentMethod(entity.getPaymentMethod())
                .paymentReference(entity.getPaymentReference())
                .createdAt(entity.getCreatedAt())
                .paidAt(entity.getPaidAt())
                .build();
    }

    public static InvoiceEntity toEntity(Invoice domain) {
        if (domain == null) {
            return null;
        }
        return InvoiceEntity.builder()
                .id(domain.getId())
                .bookingId(domain.getBookingId())
                .enrollmentId(domain.getEnrollmentId())
                .userId(domain.getUserId())
                .schoolId(domain.getSchoolId())
                .amount(domain.getAmount())
                .status(domain.getStatus())
                .paymentMethod(domain.getPaymentMethod())
                .paymentReference(domain.getPaymentReference())
                .createdAt(domain.getCreatedAt())
                .paidAt(domain.getPaidAt())
                .build();
    }
}
