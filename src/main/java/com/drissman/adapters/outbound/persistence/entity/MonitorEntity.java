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
@Table("monitors")
public class MonitorEntity {

    @Id
    private UUID id;

    @Column("school_id")
    private UUID schoolId;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    private String email;

    private String password;

    @Column("phone_number")
    private String phoneNumber;

    private String specialties;

    @Column("avatar_url")
    private String avatarUrl;

    @Column("license_number")
    private String licenseNumber;

    @Column("user_id")
    private UUID userId;

    @Column("is_active")
    @Builder.Default
    private Boolean isActive = true;

    private String status;

    @Column("created_at")
    private LocalDateTime createdAt;
}
