package com.drissman.adapters.inbound.rest.mapper;

import com.drissman.adapters.inbound.rest.dto.UserDto;
import com.drissman.domain.model.User;

public class UserRestMapper {

    private UserRestMapper() {
        // Utility class
    }

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .schoolId(user.getSchoolId())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}
