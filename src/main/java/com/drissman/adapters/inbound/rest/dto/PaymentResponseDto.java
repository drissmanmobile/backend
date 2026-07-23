package com.drissman.adapters.inbound.rest.dto;

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
public class PaymentResponseDto {
    private UUID id;
    private UUID enrollmentId;
    private String reference;
    private Integer amount;
    private String status;
    private String method;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private String checkoutUrl;
}
