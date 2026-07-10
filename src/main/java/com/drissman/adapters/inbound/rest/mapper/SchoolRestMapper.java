package com.drissman.adapters.inbound.rest.mapper;

import com.drissman.adapters.inbound.rest.dto.SchoolDto;
import com.drissman.domain.model.Offer;
import com.drissman.domain.model.School;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SchoolRestMapper {

    public static SchoolDto toDto(School domain) {
        if (domain == null) {
            return null;
        }

        List<SchoolDto.OfferDto> offerDtos = new ArrayList<>();
        Integer minPrice = null;

        if (domain.getOffers() != null) {
            offerDtos = domain.getOffers().stream()
                    .map(offer -> SchoolDto.OfferDto.builder()
                            .id(offer.getId())
                            .name(offer.getName())
                            .description(offer.getDescription())
                            .price(offer.getPrice())
                            .hours(offer.getHours())
                            .permitType(offer.getPermitType())
                            .build())
                    .toList();

            minPrice = domain.getOffers().stream()
                    .map(Offer::getPrice)
                    .min(Comparator.naturalOrder())
                    .orElse(150000); // Fallback price for demo schools
        } else if (Boolean.TRUE.equals(domain.getIsDemo())) {
            minPrice = 150000;
        }

        return SchoolDto.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .address(domain.getAddress())
                .city(domain.getCity())
                .phone(domain.getPhone())
                .email(domain.getEmail())
                .website(domain.getWebsite())
                .rating(domain.getRating())
                .imageUrl(domain.getImageUrl())
                .latitude(domain.getLatitude())
                .longitude(domain.getLongitude())
                .minPrice(minPrice)
                .offers(offerDtos)
                .build();
    }
}
