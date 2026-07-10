package com.drissman.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    private UUID id;
    private UUID bookingId;
    private UUID enrollmentId;
    private UUID userId;
    private UUID schoolId;
    private Integer amount;
    private InvoiceStatus status;
    private PaymentMethod paymentMethod;
    private String paymentReference;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public enum InvoiceStatus {
        PENDING,
        PAID,
        FAILED,
        REFUNDED
    }

    public enum PaymentMethod {
        MTN_MOMO,
        ORANGE_MONEY,
        CARD,
        CASH
    }
}
