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
public class StudentAttempt {
    private UUID id;
    private UUID exerciseId;
    private UUID studentId;
    private Integer score;
    private Integer totalQuestions;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
