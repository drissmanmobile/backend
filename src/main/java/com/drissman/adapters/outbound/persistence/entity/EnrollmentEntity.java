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
@Table("enrollments")
public class EnrollmentEntity {

    @Id
    private UUID id;

    @Column("user_id")
    private UUID userId;

    @Column("school_id")
    private UUID schoolId;

    @Column("offer_id")
    private UUID offerId;

    @Column("training_period_id")
    private UUID trainingPeriodId;

    @Column("enrolled_at")
    private LocalDateTime enrolledAt;

    private String status;

    @Column("hours_purchased")
    private Integer hoursPurchased;

    @Column("hours_consumed")
    @Builder.Default
    private Integer hoursConsumed = 0;

    @Column("created_at")
    private LocalDateTime createdAt;
}
