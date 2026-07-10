package com.drissman.adapters.inbound.rest.mapper;

import com.drissman.adapters.inbound.rest.dto.SchoolDto;
import com.drissman.domain.model.Offer;

public class OfferRestMapper {

    private OfferRestMapper() {
        // Utility class
    }

    public static SchoolDto.OfferDto toDto(Offer domain) {
        if (domain == null) {
            return null;
        }
        return SchoolDto.OfferDto.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .price(domain.getPrice())
                .hours(domain.getHours())
                .permitType(domain.getPermitType())
                .build();
    }
}
