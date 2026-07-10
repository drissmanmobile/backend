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
@Table("exercises")
public class ExerciseEntity {

    @Id
    private UUID id;

    @Column("student_id")
    private UUID studentId;

    private String title;

    @Column("is_ai_generated")
    private Boolean isAiGenerated;

    @Column("created_at")
    private LocalDateTime createdAt;
}
