package com.drissman.adapters.inbound.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String email;

    private String username;

    @NotBlank
    private String password;

    public String getIdentifier() {
        if (username != null && !username.isBlank()) {
            return username.trim();
        }
        return email != null ? email.trim() : "";
    }
}
