package com.drissman.adapters.outbound.persistence.entity;

import com.drissman.domain.model.Invoice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("invoices")
public class InvoiceEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column("booking_id")
    private UUID bookingId;

    @Column("enrollment_id")
    private UUID enrollmentId;

    @Column("user_id")
    private UUID userId;

    @Column("school_id")
    private UUID schoolId;

    private Integer amount;

    private Invoice.InvoiceStatus status;

    @Column("payment_method")
    private Invoice.PaymentMethod paymentMethod;

    @Column("payment_reference")
    private String paymentReference;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("paid_at")
    private LocalDateTime paidAt;

    @Transient
    @Builder.Default
    private boolean isNewEntity = true;

    @Override
    public boolean isNew() {
        return isNewEntity || id == null;
    }
}
