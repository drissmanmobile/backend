package com.drissman.domain.model.kernel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KernelLoginResponse {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    // Any other relevant user info retrieved from the login response
}
