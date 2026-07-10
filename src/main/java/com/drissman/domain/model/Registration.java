package com.drissman.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Registration {

    private UUID id;
    private UUID userId;
    private UUID schoolId;
    private UUID formationId;
    private String category;
    
    @Builder.Default
    private RegistrationStatus status = RegistrationStatus.PENDING;
    
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.NOT_REQUIRED;
    
    @Builder.Default
    private DocumentsStatus documentsStatus = DocumentsStatus.NOT_REQUIRED;
    
    private LocalDateTime createdAt;
    private LocalDateTime validatedAt;
    private UUID validatedBy;
    private String remarks;

    public enum RegistrationStatus {
        PENDING,
        UNDER_REVIEW,
        APPROVED,
        REJECTED
    }

    public enum PaymentStatus {
        NOT_REQUIRED,
        PENDING,
        PAID
    }

    public enum DocumentsStatus {
        NOT_REQUIRED,
        PENDING,
        VERIFIED
    }
}
