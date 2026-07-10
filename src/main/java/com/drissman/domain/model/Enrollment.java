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
public class Enrollment {

    private UUID id;
    private UUID userId;
    private UUID schoolId;
    private UUID offerId;
    private UUID trainingPeriodId;
    private LocalDateTime enrolledAt;
    private EnrollmentStatus status;
    private Integer hoursPurchased;
    @Builder.Default
    private Integer hoursConsumed = 0;
    private LocalDateTime createdAt;

    public enum EnrollmentStatus {
        PENDING,
        ACTIVE,
        SUSPENDED,
        COMPLETED,
        CANCELLED
    }

    public Integer getRemainingHours() {
        return hoursPurchased - hoursConsumed;
    }
}
