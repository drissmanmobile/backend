package com.drissman.adapters.inbound.rest.dto;

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
public class VehicleDto {
    private UUID id;
    private UUID schoolId;
    private String brand;
    private String model;
    private String registrationNumber;
    private String transmission;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
