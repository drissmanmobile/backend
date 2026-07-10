package com.drissman.adapters.inbound.rest.mapper;

import com.drissman.adapters.inbound.rest.dto.ModuleDto;
import com.drissman.domain.model.Module;

public class ModuleRestMapper {

    private ModuleRestMapper() {
        // Utility class
    }

    public static ModuleDto toDto(Module domain) {
        if (domain == null) {
            return null;
        }
        return ModuleDto.builder()
                .id(domain.getId())
                .name(domain.getName())
                .category(domain.getCategory() != null ? domain.getCategory().name() : "CODE")
                .description(domain.getDescription())
                .orderIndex(domain.getOrderIndex())
                .requiredHours(domain.getRequiredHours())
                .build();
    }
}
