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
public class Review {
    private UUID id;
    private UUID userId;
    private UUID schoolId;
    private Integer rating;
    private String comment;
    private Boolean verified;
    private LocalDateTime createdAt;
}
