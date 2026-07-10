package com.drissman.adapters.outbound.persistence.mapper;

import com.drissman.adapters.outbound.persistence.entity.OfferModuleEntity;
import com.drissman.domain.model.OfferModule;

public class OfferModulePersistenceMapper {

    private OfferModulePersistenceMapper() {
        // Utility class
    }

    public static OfferModule toDomain(OfferModuleEntity entity) {
        if (entity == null) {
            return null;
        }
        return OfferModule.builder()
                .id(entity.getId())
                .offerId(entity.getOfferId())
                .moduleId(entity.getModuleId())
                .orderIndex(entity.getOrderIndex())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static OfferModuleEntity toEntity(OfferModule domain) {
        if (domain == null) {
            return null;
        }
        return OfferModuleEntity.builder()
                .id(domain.getId())
                .offerId(domain.getOfferId())
                .moduleId(domain.getModuleId())
                .orderIndex(domain.getOrderIndex())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
