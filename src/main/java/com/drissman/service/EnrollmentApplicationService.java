package com.drissman.service;

import com.drissman.domain.model.Enrollment;
import com.drissman.domain.model.Invoice;
import com.drissman.domain.model.Offer;
import com.drissman.domain.model.User;
import com.drissman.ports.outbound.InvoiceRepositoryPort;
import com.drissman.ports.outbound.UserRepositoryPort;
import com.drissman.ports.inbound.EnrollmentUseCase;
import com.drissman.ports.outbound.EnrollmentRepositoryPort;
import com.drissman.ports.outbound.OfferRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@RequiredArgsConstructor
public class EnrollmentApplicationService implements EnrollmentUseCase {

    private final EnrollmentRepositoryPort enrollmentRepositoryPort;
    private final OfferRepositoryPort offerRepositoryPort;
    private final UserRepositoryPort userRepository;
    private final InvoiceRepositoryPort invoiceRepositoryPort;

    @Override
    public Mono<Enrollment> createEnrollment(UUID userId, UUID offerId) {
        Mono<User> userMono = userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable")));

        Mono<Offer> offerMono = offerRepositoryPort.findById(offerId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Offre introuvable")));

        return Mono.zip(userMono, offerMono)
                .flatMap(tuple -> {
                    User user = tuple.getT1();
                    Offer offer = tuple.getT2();

                    return enrollmentRepositoryPort.findByUserIdAndOfferId(userId, offerId)
                            .filter(existing -> existing.getStatus() == Enrollment.EnrollmentStatus.PENDING
                                    || existing.getStatus() == Enrollment.EnrollmentStatus.ACTIVE)
                            .flatMap(existing -> {
                                if (existing.getStatus() == Enrollment.EnrollmentStatus.PENDING) {
                                    return Mono.just(existing);
                                }
                                return Mono.<Enrollment>error(new ResponseStatusException(HttpStatus.CONFLICT,
                                        "Vous etes deja inscrit a cette offre"));
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                Mono<User> normalizedUserMono;
                                if (user.getRole() == User.Role.VISITOR || user.getSchoolId() == null) {
                                    user.setRole(User.Role.STUDENT);
                                    user.setSchoolId(offer.getSchoolId());
                                    normalizedUserMono = userRepository.save(user);
                                } else {
                                    normalizedUserMono = Mono.just(user);
                                }

                                return normalizedUserMono.flatMap(savedUser -> {
                                    Enrollment enrollment = Enrollment.builder()
                                            .userId(savedUser.getId())
                                            .schoolId(offer.getSchoolId())
                                            .offerId(offer.getId())
                                            .trainingPeriodId(null)
                                            .enrolledAt(LocalDateTime.now())
                                            .status(Enrollment.EnrollmentStatus.PENDING)
                                            .hoursPurchased(offer.getHours() != null ? offer.getHours() : 0)
                                            .hoursConsumed(0)
                                            .createdAt(LocalDateTime.now())
                                            .build();

                                    return enrollmentRepositoryPort.save(enrollment)
                                            .flatMap(savedEnrollment -> {
                                                Invoice invoice = Invoice.builder()
                                                        .enrollmentId(savedEnrollment.getId())
                                                        .userId(savedUser.getId())
                                                        .schoolId(offer.getSchoolId())
                                                        .amount(offer.getPrice() != null ? offer.getPrice() : 0)
                                                        .status(Invoice.InvoiceStatus.PENDING)
                                                        .paymentMethod(Invoice.PaymentMethod.CASH)
                                                        .paymentReference("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                                                        .createdAt(LocalDateTime.now())
                                                        .build();
                                                return invoiceRepositoryPort.save(invoice).thenReturn(savedEnrollment);
                                            });
                                });
                            }));
                });
    }

    @Override
    public Flux<Enrollment> getEnrollmentsForStudent(UUID userId) {
        return enrollmentRepositoryPort.findByUserId(userId);
    }

    @Override
    public Flux<Enrollment> getEnrollmentsForSchool(UUID schoolId) {
        return enrollmentRepositoryPort.findBySchoolId(schoolId);
    }

    @Override
    public Mono<Enrollment> updateEnrollmentStatus(UUID schoolId, UUID enrollmentId, String statusRaw) {
        Enrollment.EnrollmentStatus nextStatus;
        try {
            nextStatus = Enrollment.EnrollmentStatus.valueOf(statusRaw.trim().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Statut invalide"));
        }

        return enrollmentRepositoryPort.findById(enrollmentId)
                .filter(enrollment -> schoolId.equals(enrollment.getSchoolId()))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscription introuvable")))
                .flatMap(enrollment -> {
                    enrollment.setStatus(nextStatus);
                    return enrollmentRepositoryPort.save(enrollment);
                });
    }
}
