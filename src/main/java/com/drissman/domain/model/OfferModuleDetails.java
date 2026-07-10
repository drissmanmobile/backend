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
public class OfferModuleDetails {
    private UUID id;
    private UUID offerId;
    private UUID moduleId;
    private Integer orderIndex;
    private String moduleName;
    private String moduleCategory;
    private String moduleDescription;
    private Integer moduleRequiredHours;
}
