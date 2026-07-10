package com.drissman.adapters.outbound.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class UserEntity {

    @Id
    private UUID id;

    private String email;

    private String password;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    private String phone;

    @Column("role")
    private String role;

    @Column("school_id")
    private UUID schoolId;

    @Column("avatar_url")
    private String avatarUrl;

    @Column("is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column("is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @Column("otp_code")
    private String otpCode;

    @Column("otp_expiry")
    private LocalDateTime otpExpiry;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
