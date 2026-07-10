package com.drissman.adapters.inbound.rest.dto;

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
public class RegistrationDto {
    private UUID id;
    private UUID userId;
    private UUID schoolId;
    private UUID formationId;
    private String category;
    private String status;
    private String paymentStatus;
    private String documentsStatus;
    private LocalDateTime createdAt;
    private LocalDateTime validatedAt;
    private UUID validatedBy;
    private String remarks;
}
