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
public class DeviceToken {
    private UUID id;
    private UUID userId;
    private String token;
    private String platform;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
