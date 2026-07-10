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
public class OfferModule {
    private UUID id;
    private UUID offerId;
    private UUID moduleId;
    private Integer orderIndex;
    private LocalDateTime createdAt;
}
