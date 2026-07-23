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
@Table("notifications")
public class NotificationEntity {

    @Id
    private UUID id;

    @Column("user_id")
    private UUID userId;

    @Column("type")
    private String type;

    @Column("title")
    private String title;

    @Column("message")
    private String message;

    @Column("date_str")
    private String dateStr;

    @Column("read")
    private boolean read;

    @Column("created_at")
    private LocalDateTime createdAt;
}
