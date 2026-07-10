package com.drissman.adapters.inbound.rest.mapper;

import com.drissman.adapters.inbound.rest.dto.ReviewDto;
import com.drissman.domain.model.ReviewDetails;

public class ReviewRestMapper {

    private ReviewRestMapper() {
        // Utility class
    }

    public static ReviewDto toDto(ReviewDetails domain) {
        if (domain == null) {
            return null;
        }
        return ReviewDto.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .userName(domain.getUserName())
                .schoolId(domain.getSchoolId())
                .rating(domain.getRating())
                .comment(domain.getComment())
                .verified(domain.getVerified())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
