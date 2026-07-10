package com.drissman.adapters.inbound.rest.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SetOfferModulesRequest {
    private List<ModuleEntry> modules;

    @Data
    public static class ModuleEntry {
        private UUID moduleId;
        private Integer orderIndex;
    }
}
