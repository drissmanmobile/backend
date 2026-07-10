package com.drissman.adapters.inbound.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpgradeVisitorRoleRequest {
    @NotBlank
    private String targetRole;

    private String schoolName;
}
