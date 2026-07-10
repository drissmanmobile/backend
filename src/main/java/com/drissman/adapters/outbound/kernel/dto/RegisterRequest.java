package com.drissman.adapters.outbound.kernel.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequest {
    private String actorId;
    private String username;
    private String email;
    private String phoneNumber;
    private String password;
    private String authProvider;
    private String externalSubject;
}
