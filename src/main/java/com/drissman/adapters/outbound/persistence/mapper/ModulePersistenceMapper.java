package com.drissman.adapters.outbound.persistence.mapper;

import com.drissman.adapters.outbound.persistence.entity.ModuleEntity;
import com.drissman.domain.model.Module;

public class ModulePersistenceMapper {

    private ModulePersistenceMapper() {
        // Utility class
    }

    public static Module toDomain(ModuleEntity entity) {
        if (entity == null) {
            return null;
        }

        Module.ModuleCategory category;
        try {
            category = entity.getCategory() != null ? Module.ModuleCategory.valueOf(entity.getCategory().toUpperCase()) : null;
        } catch (IllegalArgumentException e) {
            category = Module.ModuleCategory.CODE;
        }

        return Module.builder()
                .id(entity.getId())
                .schoolId(entity.getSchoolId())
                .name(entity.getName())
                .category(category)
                .description(entity.getDescription())
                .orderIndex(entity.getOrderIndex())
                .requiredHours(entity.getRequiredHours())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static ModuleEntity toEntity(Module domain) {
        if (domain == null) {
            return null;
        }
        return ModuleEntity.builder()
                .id(domain.getId())
                .schoolId(domain.getSchoolId())
                .name(domain.getName())
                .category(domain.getCategory() != null ? domain.getCategory().name() : null)
                .description(domain.getDescription())
                .orderIndex(domain.getOrderIndex())
                .requiredHours(domain.getRequiredHours())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
