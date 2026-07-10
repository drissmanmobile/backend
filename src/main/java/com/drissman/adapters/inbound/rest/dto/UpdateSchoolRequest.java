package com.drissman.adapters.inbound.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSchoolRequest {
    private String name;
    private String description;
    private String address;
    private String city;
    private String region;
    private String phone;
    private String email;
    private String website;
    private String imageUrl;
}
