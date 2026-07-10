package com.drissman.adapters.outbound.kernel.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateActorRequest {
    private String organizationId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String type; // e.g., "PERSON"
    private String gender;
}
