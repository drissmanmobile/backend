package com.drissman.adapters.outbound.persistence.mapper;

import com.drissman.adapters.outbound.persistence.entity.ReviewEntity;
import com.drissman.domain.model.Review;

public class ReviewPersistenceMapper {

    private ReviewPersistenceMapper() {
        // Utility class
    }

    public static Review toDomain(ReviewEntity entity) {
        if (entity == null) {
            return null;
        }
        return Review.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .schoolId(entity.getSchoolId())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .verified(entity.getVerified())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static ReviewEntity toEntity(Review domain) {
        if (domain == null) {
            return null;
        }
        return ReviewEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .schoolId(domain.getSchoolId())
                .rating(domain.getRating())
                .comment(domain.getComment())
                .verified(domain.getVerified())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
