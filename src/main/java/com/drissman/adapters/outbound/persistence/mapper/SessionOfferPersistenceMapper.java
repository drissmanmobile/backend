package com.drissman.adapters.outbound.persistence.mapper;

import com.drissman.adapters.outbound.persistence.entity.SessionOfferEntity;
import com.drissman.domain.model.SessionOffer;

public class SessionOfferPersistenceMapper {

    private SessionOfferPersistenceMapper() {
        // Utility class
    }

    public static SessionOffer toDomain(SessionOfferEntity entity) {
        if (entity == null) {
            return null;
        }
        return SessionOffer.builder()
                .id(entity.getId())
                .trainingPeriodId(entity.getTrainingPeriodId())
                .offerId(entity.getOfferId())
                .maxStudentsOverride(entity.getMaxStudentsOverride())
                .priceOverride(entity.getPriceOverride())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static SessionOfferEntity toEntity(SessionOffer domain) {
        if (domain == null) {
            return null;
        }
        return SessionOfferEntity.builder()
                .id(domain.getId())
                .trainingPeriodId(domain.getTrainingPeriodId())
                .offerId(domain.getOfferId())
                .maxStudentsOverride(domain.getMaxStudentsOverride())
                .priceOverride(domain.getPriceOverride())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
