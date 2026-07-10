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
public class SessionOffer {
    private UUID id;
    private UUID trainingPeriodId;
    private UUID offerId;
    private Integer maxStudentsOverride;
    private Integer priceOverride;
    private LocalDateTime createdAt;
}
