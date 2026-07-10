package com.drissman.adapters.inbound.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRegistrationRequest {
    private UUID schoolId;
    private UUID formationId;
    private String category;
    private String remarks;
    // other fields requested by user (phone, cni, birthdate, address)
    // The user said: "téléphone, numéro CNI, date de naissance, adresse"
    private String phone;
    private String cniNumber;
    private String birthDate;
    private String address;
}
