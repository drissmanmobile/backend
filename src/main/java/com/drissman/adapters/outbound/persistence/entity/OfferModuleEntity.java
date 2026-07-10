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
@Table("offer_modules")
public class OfferModuleEntity {

    @Id
    private UUID id;

    @Column("offer_id")
    private UUID offerId;

    @Column("module_id")
    private UUID moduleId;

    @Column("order_index")
    private Integer orderIndex;

    @Column("created_at")
    private LocalDateTime createdAt;
}
