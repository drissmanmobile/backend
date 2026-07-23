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
@Table("session_locations")
public class SessionLocationEntity {

    @Id
    private UUID id;

    @Column("session_id")
    private UUID sessionId;

    @Column("vehicle_id")
    private UUID vehicleId;

    @Column("latitude")
    private Double latitude;

    @Column("longitude")
    private Double longitude;

    @Column("speed")
    private Double speed;

    @Column("heading")
    private Double heading;

    @Column("created_at")
    private LocalDateTime createdAt;
}
