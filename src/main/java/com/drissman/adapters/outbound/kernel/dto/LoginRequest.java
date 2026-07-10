package com.drissman.adapters.outbound.kernel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {
    private String principal;
    private String password;
}
