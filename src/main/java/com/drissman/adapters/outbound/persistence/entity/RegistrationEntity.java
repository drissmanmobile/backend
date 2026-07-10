package com.drissman.adapters.outbound.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("registrations")
public class RegistrationEntity {
    @Id
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
