package com.drissman.domain.model.kernel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KernelRegisterResponse {
    private String userId;
    private String username;
    private String email;
}
