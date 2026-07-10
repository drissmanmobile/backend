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
public class Document {

    private UUID id;
    private String name;
    private String fileUrl;
    private UUID uploaderId;
    private UUID moduleId;
    private UUID sessionId;
    private UUID schoolId;
    private UUID offerId;
    private LocalDateTime createdAt;
}
