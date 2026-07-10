package com.drissman.adapters.inbound.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleLoginRequest {
    @NotBlank(message = "L'idToken est obligatoire")
    private String idToken;
}
