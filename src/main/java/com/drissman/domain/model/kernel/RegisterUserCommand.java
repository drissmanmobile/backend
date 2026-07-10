package com.drissman.domain.model.kernel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterUserCommand {
    private String username;
    private String email;
    private String phoneNumber;
    private String password;
    private String authProvider; // ex: "LOCAL", "GOOGLE"
    private String externalSubject;
}
