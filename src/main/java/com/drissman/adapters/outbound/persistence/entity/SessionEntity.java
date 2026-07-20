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
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("sessions")
public class SessionEntity {

    @Id
    private UUID id;

    @Column("enrollment_id")
    private UUID enrollmentId;

    @Column("offer_id")
    private UUID offerId;

    @Column("monitor_id")
    private UUID monitorId;

    @Column("vehicle_id")
    private UUID vehicleId;

    @Column("module_id")
    private UUID moduleId;

    @Column("lesson_id")
    private UUID lessonId;

    private LocalDate date;

    @Column("start_time")
    private LocalTime startTime;

    @Column("end_time")
    private LocalTime endTime;

    private String status;

    @Column("meeting_point")
    private String meetingPoint;

    @Column("pedagogical_notes")
    private String pedagogicalNotes;

    @Column("created_at")
    private LocalDateTime createdAt;
}
