package com.drissman.adapters.outbound.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("student_answers")
public class StudentAnswerEntity {

    @Id
    private UUID id;

    @Column("attempt_id")
    private UUID attemptId;

    @Column("question_id")
    private UUID questionId;

    @Column("choice_id")
    private UUID choiceId;

    @Column("is_correct")
    private Boolean isCorrect;
}
