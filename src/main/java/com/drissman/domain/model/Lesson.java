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
public class Lesson {
    private UUID id;
    private UUID schoolId;
    private UUID monitorId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String topic;
    private LessonType lessonType;
    private UUID moduleId;
    private UUID trainingPeriodId;
    private String description;
    private String roomId;
    private Integer capacity;
    private LessonStatus status;
    private LocalDateTime createdAt;

    public enum LessonStatus {
        SCHEDULED,
        CANCELLED,
        COMPLETED
    }

    public enum LessonType {
        CODE,
        CONDUITE,
        EXAMEN_BLANC
    }
}
