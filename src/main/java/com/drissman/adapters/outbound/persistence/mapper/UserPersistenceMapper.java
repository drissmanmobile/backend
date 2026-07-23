package com.drissman.adapters.outbound.persistence.mapper;

import com.drissman.adapters.outbound.persistence.entity.UserEntity;
import com.drissman.domain.model.User;

public class UserPersistenceMapper {

    private UserPersistenceMapper() {
        // Utility class
    }

    public static User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .phone(entity.getPhone())
                .role(entity.getRole() != null ? User.Role.valueOf(entity.getRole()) : null)
                .schoolId(entity.getSchoolId())
                .avatarUrl(entity.getAvatarUrl())
                .isActive(entity.getIsActive())
                .isVerified(entity.getIsVerified())
                .otpCode(entity.getOtpCode())
                .otpExpiry(entity.getOtpExpiry())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }
        return UserEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .username(domain.getUsername())
                .password(domain.getPassword())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .phone(domain.getPhone())
                .role(domain.getRole() != null ? domain.getRole().name() : null)
                .schoolId(domain.getSchoolId())
                .avatarUrl(domain.getAvatarUrl())
                .isActive(domain.getIsActive())
                .isVerified(domain.getIsVerified())
                .otpCode(domain.getOtpCode())
                .otpExpiry(domain.getOtpExpiry())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
