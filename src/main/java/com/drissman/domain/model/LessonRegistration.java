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
public class LessonRegistration {
    private UUID id;
    private UUID lessonId;
    private UUID studentId;
    private RegistrationStatus status;
    private LocalDateTime attendedAt;
    private String notes;
    private LocalDateTime createdAt;

    public enum RegistrationStatus {
        REGISTERED,
        ATTENDED,
        ABSENT,
        CANCELLED
    }
}
