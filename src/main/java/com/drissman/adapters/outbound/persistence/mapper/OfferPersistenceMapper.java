package com.drissman.adapters.outbound.persistence.mapper;

import com.drissman.adapters.outbound.persistence.entity.OfferEntity;
import com.drissman.domain.model.Offer;

public class OfferPersistenceMapper {

    private OfferPersistenceMapper() {
        // Utility class
    }

    public static Offer toDomain(OfferEntity entity) {
        if (entity == null) {
            return null;
        }
        return Offer.builder()
                .id(entity.getId())
                .schoolId(entity.getSchoolId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .hours(entity.getHours())
                .permitType(entity.getPermitType())
                .build();
    }

    public static OfferEntity toEntity(Offer domain) {
        if (domain == null) {
            return null;
        }
        return OfferEntity.builder()
                .id(domain.getId())
                .schoolId(domain.getSchoolId())
                .name(domain.getName())
                .description(domain.getDescription())
                .price(domain.getPrice())
                .hours(domain.getHours())
                .permitType(domain.getPermitType())
                .build();
    }
}
