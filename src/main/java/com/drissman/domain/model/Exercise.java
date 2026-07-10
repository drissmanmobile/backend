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
public class Exercise {
    private UUID id;
    private UUID studentId;
    private String title;
    private Boolean isAiGenerated;
    private LocalDateTime createdAt;
}
