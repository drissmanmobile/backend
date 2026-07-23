package com.drissman.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID id;
    private String email;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private Role role;
    private UUID schoolId;
    private UUID classId;
    private UUID monitorId;
    private String cniNumber;
    private java.time.LocalDate birthDate;
    private String address;
    private String avatarUrl;
    @Builder.Default
    private Boolean isActive = true;
    @Builder.Default
    private Boolean isVerified = false;
    private String otpCode;
    private LocalDateTime otpExpiry;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Role {
        VISITOR,
        STUDENT,
        SCHOOL_ADMIN,
        MONITOR
    }
}
