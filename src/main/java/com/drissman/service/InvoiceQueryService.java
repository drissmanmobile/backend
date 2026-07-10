package com.drissman.service;

import com.drissman.adapters.inbound.rest.dto.InvoiceViewDto;
import com.drissman.domain.model.Enrollment;
import com.drissman.domain.model.Offer;
import com.drissman.domain.model.User;
import com.drissman.ports.outbound.UserRepositoryPort;
import com.drissman.ports.inbound.EnrollmentUseCase;
import com.drissman.ports.outbound.OfferRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceQueryService {

    private final EnrollmentUseCase enrollmentUseCase;
    private final OfferRepositoryPort offerRepositoryPort;
    private final UserRepositoryPort userRepository;

    public Flux<InvoiceViewDto> getInvoicesForSchool(UUID schoolId) {
        LocalDateTime now = LocalDateTime.now();
        return enrollmentUseCase.getEnrollmentsForSchool(schoolId)
                .filter(e -> e.getStatus() != Enrollment.EnrollmentStatus.CANCELLED)
                .flatMap(this::enrich)
                .index()
                .map(indexed -> {
                    long idx = indexed.getT1() + 1;
                    var enriched = indexed.getT2();
                    var e = enriched.enrollment;
                    var offer = enriched.offer;
                    var user = enriched.user;

                    LocalDateTime dueDate = e.getEnrolledAt().plusDays(7);
                    String status = mapInvoiceStatus(e.getStatus(), dueDate, now);
                    String studentName = ((user.getFirstName() != null ? user.getFirstName() : "") + " " +
                            (user.getLastName() != null ? user.getLastName() : "")).trim();
                    if (studentName.isEmpty()) studentName = "Eleve Inconnu";

                    return InvoiceViewDto.builder()
                            .id(e.getId())
                            .enrollmentId(e.getId())
                            .invoiceNumber(String.format(Locale.ROOT, "INV-%d-%04d", e.getEnrolledAt().getYear(), idx))
                            .studentName(studentName)
                            .offer(offer.getName())
                            .amount(offer.getPrice() != null ? offer.getPrice() : 0)
                            .status(status)
                            .dueDate(dueDate)
                            .paidAt("PAID".equals(status) ? e.getEnrolledAt() : null)
                            .build();
                })
                .sort(Comparator.comparing((InvoiceViewDto i) -> i.getDueDate().toEpochSecond(ZoneOffset.UTC)).reversed());
    }

    private Mono<EnrichedEnrollment> enrich(Enrollment enrollment) {
        Mono<Offer> offerMono = offerRepositoryPort.findById(enrollment.getOfferId())
                .defaultIfEmpty(Offer.builder()
                        .name("Offre")
                        .price(0)
                        .build());
        Mono<User> userMono = userRepository.findById(enrollment.getUserId())
                .defaultIfEmpty(User.builder()
                        .firstName("Eleve")
                        .lastName("Inconnu")
                        .build());
        return Mono.zip(offerMono, userMono)
                .map(tuple -> new EnrichedEnrollment(enrollment, tuple.getT1(), tuple.getT2()));
    }

    private static class EnrichedEnrollment {
        final Enrollment enrollment;
        final Offer offer;
        final User user;
        EnrichedEnrollment(Enrollment enrollment, Offer offer, User user) {
            this.enrollment = enrollment;
            this.offer = offer;
            this.user = user;
        }
    }

    private String mapInvoiceStatus(Enrollment.EnrollmentStatus status, LocalDateTime dueDate, LocalDateTime now) {
        if (status == Enrollment.EnrollmentStatus.ACTIVE || status == Enrollment.EnrollmentStatus.COMPLETED) {
            return "PAID";
        }
        if (dueDate.isBefore(now)) {
            return "OVERDUE";
        }
        return "PENDING";
    }
}
