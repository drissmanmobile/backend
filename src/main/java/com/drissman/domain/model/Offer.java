package com.drissman.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Offer {
    private UUID id;
    private UUID schoolId;
    private String name;
    private String description;
    private Integer price;
    private Integer hours;
    private String permitType;
}
