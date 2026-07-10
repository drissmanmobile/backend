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
@Table("choices")
public class ChoiceEntity {

    @Id
    private UUID id;

    @Column("question_id")
    private UUID questionId;

    private String content;

    @Column("is_correct")
    private Boolean isCorrect;
}
