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
@Table("documents")
public class DocumentEntity {

    @Id
    private UUID id;

    private String name;

    @Column("file_url")
    private String fileUrl;

    @Column("uploader_id")
    private UUID uploaderId;

    @Column("module_id")
    private UUID moduleId;

    @Column("session_id")
    private UUID sessionId;

    @Column("school_id")
    private UUID schoolId;

    @Column("offer_id")
    private UUID offerId;

    @Column("created_at")
    private LocalDateTime createdAt;
}
