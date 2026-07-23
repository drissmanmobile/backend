package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.ApiResponse;
import com.drissman.adapters.inbound.rest.dto.InitiatePaymentRequestDto;
import com.drissman.adapters.inbound.rest.dto.PaymentResponseDto;
import com.drissman.domain.model.Enrollment;
import com.drissman.domain.model.Invoice;
import com.drissman.domain.model.Offer;
import com.drissman.ports.outbound.EnrollmentRepositoryPort;
import com.drissman.ports.outbound.InvoiceRepositoryPort;
import com.drissman.ports.outbound.OfferRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final InvoiceRepositoryPort invoiceRepository;
    private final EnrollmentRepositoryPort enrollmentRepository;
    private final OfferRepositoryPort offerRepository;

    @GetMapping("/me")
    public Mono<ApiResponse<List<PaymentResponseDto>>> getMyPayments(Principal principal) {
        if (principal == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise"));
        }
        UUID userId = UUID.fromString(principal.getName());
        return invoiceRepository.findByUserId(userId)
                .map(this::toPaymentResponseDto)
                .collectList()
                .map(ApiResponse::ok);
    }

    @PostMapping("/initiate")
    public Mono<ApiResponse<PaymentResponseDto>> initiatePayment(@RequestBody InitiatePaymentRequestDto request, Principal principal) {
        if (principal == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise"));
        }
        if (request.getEnrollmentId() == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'ID de l'inscription est requis"));
        }
        UUID userId = UUID.fromString(principal.getName());

        return enrollmentRepository.findById(request.getEnrollmentId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscription introuvable")))
                .flatMap(enrollment -> {
                    if (!userId.equals(enrollment.getUserId())) {
                        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Non autorisé"));
                    }

                    return invoiceRepository.findByEnrollmentId(enrollment.getId())
                            .filter(inv -> inv.getStatus() == Invoice.InvoiceStatus.PENDING || inv.getStatus() == Invoice.InvoiceStatus.PAID)
                            .next()
                            .flatMap(existingInvoice -> {
                                if (request.getMethod() != null) {
                                    try {
                                        existingInvoice.setPaymentMethod(Invoice.PaymentMethod.valueOf(request.getMethod().toUpperCase()));
                                    } catch (Exception ignored) {}
                                    return invoiceRepository.save(existingInvoice);
                                }
                                return Mono.just(existingInvoice);
                            })
                            .switchIfEmpty(Mono.defer(() -> createNewInvoice(enrollment, request)));
                })
                .map(this::toPaymentResponseDto)
                .map(ApiResponse::ok);
    }

    @GetMapping("/{invoiceId}/refresh")
    public Mono<ApiResponse<PaymentResponseDto>> refreshPayment(@PathVariable UUID invoiceId, Principal principal) {
        if (principal == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise"));
        }
        return invoiceRepository.findById(invoiceId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture introuvable")))
                .flatMap(invoice -> {
                    if (invoice.getStatus() == Invoice.InvoiceStatus.PENDING) {
                        return enrollmentRepository.findById(invoice.getEnrollmentId())
                                .flatMap(enrollment -> {
                                    if (enrollment.getStatus() == Enrollment.EnrollmentStatus.ACTIVE || enrollment.getStatus() == Enrollment.EnrollmentStatus.COMPLETED) {
                                        invoice.setStatus(Invoice.InvoiceStatus.PAID);
                                        invoice.setPaidAt(LocalDateTime.now());
                                        return invoiceRepository.save(invoice);
                                    }
                                    return Mono.just(invoice);
                                })
                                .defaultIfEmpty(invoice);
                    }
                    return Mono.just(invoice);
                })
                .map(this::toPaymentResponseDto)
                .map(ApiResponse::ok);
    }

    private Mono<Invoice> createNewInvoice(Enrollment enrollment, InitiatePaymentRequestDto request) {
        Invoice.PaymentMethod method = Invoice.PaymentMethod.CASH;
        if (request.getMethod() != null) {
            try {
                method = Invoice.PaymentMethod.valueOf(request.getMethod().toUpperCase());
            } catch (Exception ignored) {}
        }

        Invoice.PaymentMethod finalMethod = method;
        return offerRepository.findById(enrollment.getOfferId())
                .map(offer -> offer.getPrice() != null ? offer.getPrice() : 0)
                .defaultIfEmpty(0)
                .flatMap(amount -> {
                    Invoice invoice = Invoice.builder()
                            .enrollmentId(enrollment.getId())
                            .userId(enrollment.getUserId())
                            .schoolId(enrollment.getSchoolId())
                            .amount(amount)
                            .status(Invoice.InvoiceStatus.PENDING)
                            .paymentMethod(finalMethod)
                            .paymentReference("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                            .createdAt(LocalDateTime.now())
                            .build();
                    return invoiceRepository.save(invoice);
                });
    }

    private PaymentResponseDto toPaymentResponseDto(Invoice invoice) {
        return PaymentResponseDto.builder()
                .id(invoice.getId())
                .enrollmentId(invoice.getEnrollmentId())
                .reference(invoice.getPaymentReference() != null ? invoice.getPaymentReference() : "INV-" + invoice.getId().toString().substring(0, 8).toUpperCase())
                .amount(invoice.getAmount())
                .status(invoice.getStatus() != null ? invoice.getStatus().name() : "PENDING")
                .method(invoice.getPaymentMethod() != null ? invoice.getPaymentMethod().name() : null)
                .createdAt(invoice.getCreatedAt())
                .paidAt(invoice.getPaidAt())
                .checkoutUrl(null)
                .build();
    }
}
