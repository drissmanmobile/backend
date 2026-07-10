package com.drissman.adapters.inbound.rest.dto;

import com.drissman.domain.model.Monitor.MonitorStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitorDto {
    private UUID id;
    private UUID schoolId;
    private String firstName;
    private String lastName;
    private String licenseNumber;
    private String phoneNumber;
    private UUID userId;
    private MonitorStatus status;
}
