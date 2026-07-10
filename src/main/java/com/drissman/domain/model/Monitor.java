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
public class Monitor {

    private UUID id;
    private UUID schoolId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String specialties;
    private String avatarUrl;
    private String licenseNumber;
    private UUID userId;
    @Builder.Default
    private Boolean isActive = true;
    private MonitorStatus status;
    private LocalDateTime createdAt;

    public enum MonitorStatus {
        ACTIVE,
        INACTIVE,
        ON_LEAVE
    }
}
