package com.drissman.adapters.outbound.persistence.mapper;

import com.drissman.adapters.outbound.persistence.entity.SchoolEntity;
import com.drissman.domain.model.School;

import java.util.ArrayList;

public class SchoolPersistenceMapper {

    public static School toDomain(SchoolEntity entity) {
        if (entity == null) {
            return null;
        }
        return School.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .address(entity.getAddress())
                .city(entity.getCity())
                .region(entity.getRegion())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .website(entity.getWebsite())
                .rating(entity.getRating())
                .imageUrl(entity.getImageUrl())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .isVerified(entity.getIsVerified())
                .isDemo(entity.getIsDemo())
                .createdAt(entity.getCreatedAt())
                .offers(new ArrayList<>()) // Initialise la liste vide
                .build();
    }

    public static SchoolEntity toEntity(School domain) {
        if (domain == null) {
            return null;
        }
        return SchoolEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .address(domain.getAddress())
                .city(domain.getCity())
                .region(domain.getRegion())
                .phone(domain.getPhone())
                .email(domain.getEmail())
                .website(domain.getWebsite())
                .rating(domain.getRating())
                .imageUrl(domain.getImageUrl())
                .latitude(domain.getLatitude())
                .longitude(domain.getLongitude())
                .isVerified(domain.getIsVerified())
                .isDemo(domain.getIsDemo())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
