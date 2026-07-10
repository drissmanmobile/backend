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
@Table("student_attempts")
public class StudentAttemptEntity {

    @Id
    private UUID id;

    @Column("exercise_id")
    private UUID exerciseId;

    @Column("student_id")
    private UUID studentId;

    private Integer score;

    @Column("total_questions")
    private Integer totalQuestions;

    @Column("started_at")
    private LocalDateTime startedAt;

    @Column("completed_at")
    private LocalDateTime completedAt;
}
