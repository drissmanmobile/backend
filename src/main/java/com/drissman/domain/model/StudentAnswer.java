package com.drissman.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAnswer {
    private UUID id;
    private UUID attemptId;
    private UUID questionId;
    private UUID choiceId;
    private Boolean isCorrect;
}
