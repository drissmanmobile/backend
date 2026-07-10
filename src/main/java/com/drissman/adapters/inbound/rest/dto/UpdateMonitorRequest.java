package com.drissman.adapters.inbound.rest.dto;

import com.drissman.domain.model.Monitor.MonitorStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMonitorRequest {
    private String firstName;
    private String lastName;
    private String licenseNumber;
    private String phoneNumber;
    private MonitorStatus status;
}
