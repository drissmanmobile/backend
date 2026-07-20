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
public class Vehicle {

    private UUID id;
    private UUID schoolId;
    private String brand;
    private String model;
    private String registrationNumber;
    private Transmission transmission;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Transmission {
        MANUAL,
        AUTOMATIC
    }

    public enum Status {
        ACTIVE,
        IN_MAINTENANCE,
        INACTIVE
    }
}
