package com.drissman.adapters.outbound.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("training_periods")
public class TrainingPeriodEntity {

    @Id
    private UUID id;

    @Column("school_id")
    private UUID schoolId;

    @Column("offer_id")
    private UUID offerId;

    private String name;

    private String description;

    @Column("start_date")
    private LocalDate startDate;

    @Column("end_date")
    private LocalDate endDate;

    @Column("max_students")
    @Builder.Default
    private Integer maxStudents = 30;

    private String status;

    @Column("enrollment_deadline")
    private LocalDate enrollmentDeadline;

    @Column("schedule_description")
    private String scheduleDescription;

    @Column("created_at")
    private LocalDateTime createdAt;
}
