package com.drissman.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingPeriod {

    private UUID id;
    private UUID schoolId;
    private UUID offerId;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    @Builder.Default
    private Integer maxStudents = 30;
    private TrainingPeriodStatus status;
    private LocalDate enrollmentDeadline;
    private String scheduleDescription;
    private LocalDateTime createdAt;

    public enum TrainingPeriodStatus {
        DRAFT,
        PUBLISHED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
}
