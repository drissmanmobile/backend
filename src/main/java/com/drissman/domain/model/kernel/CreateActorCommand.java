package com.drissman.domain.model.kernel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateActorCommand {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String type; // e.g., "PERSON"
    private String gender;
}
