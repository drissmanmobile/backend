package com.drissman.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    private UUID id;
    private UUID enrollmentId;
    private UUID offerId;
    private UUID monitorId;
    private UUID moduleId;
    private UUID lessonId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private SessionStatus status;
    private String meetingPoint;
    private String pedagogicalNotes;
    private LocalDateTime createdAt;

    public enum SessionStatus {
        SCHEDULED,
        CONFIRMED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        NO_SHOW
    }

    public int getDurationHours() {
        if (startTime == null || endTime == null)
            return 0;
        return (int) java.time.Duration.between(startTime, endTime).toHours();
    }
}
