package com.drissman.service;

import com.drissman.domain.model.SessionOffer;
import com.drissman.domain.model.TrainingPeriod;
import com.drissman.domain.model.TrainingPeriodDetails;
import com.drissman.domain.model.TrainingPeriodFormation;
import com.drissman.ports.inbound.TrainingPeriodUseCase;
import com.drissman.ports.outbound.EnrollmentRepositoryPort;
import com.drissman.ports.outbound.OfferRepositoryPort;
import com.drissman.ports.outbound.TrainingPeriodRepositoryPort;
import com.drissman.ports.outbound.SessionOfferRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RequiredArgsConstructor
public class TrainingPeriodApplicationService implements TrainingPeriodUseCase {

    private final TrainingPeriodRepositoryPort trainingPeriodRepository;
    private final SessionOfferRepositoryPort sessionOfferRepository;
    private final OfferRepositoryPort offerRepository;
    private final EnrollmentRepositoryPort enrollmentRepository;

    @Override
    public Flux<TrainingPeriodDetails> getBySchool(UUID schoolId) {
        return trainingPeriodRepository.findBySchoolId(schoolId)
                .flatMap(this::toDetails)
                .sort((a, b) -> b.getStartDate().compareTo(a.getStartDate()));
    }

    @Override
    public Flux<TrainingPeriodDetails> getPublishedBySchool(UUID schoolId) {
        return trainingPeriodRepository.findBySchoolId(schoolId)
                .filter(period -> period.getStatus() == TrainingPeriod.TrainingPeriodStatus.PUBLISHED
                        || period.getStatus() == TrainingPeriod.TrainingPeriodStatus.IN_PROGRESS)
                .flatMap(this::toDetails)
                .sort((a, b) -> b.getStartDate().compareTo(a.getStartDate()));
    }

    @Override
    @Transactional
    public Mono<TrainingPeriodDetails> create(UUID schoolId, String name, String description, LocalDate startDate, LocalDate endDate,
                                              Integer maxStudents, LocalDate enrollmentDeadline, String scheduleDescription,
                                              UUID offerId, List<UUID> offerIds) {
        UUID primaryOfferId = resolvePrimaryOfferId(offerId, offerIds);
        TrainingPeriod period = TrainingPeriod.builder()
                .schoolId(schoolId)
                .offerId(primaryOfferId)
                .name(name)
                .description(description)
                .startDate(startDate)
                .endDate(endDate)
                .maxStudents(maxStudents != null ? maxStudents : 30)
                .status(TrainingPeriod.TrainingPeriodStatus.PUBLISHED)
                .enrollmentDeadline(enrollmentDeadline)
                .scheduleDescription(scheduleDescription)
                .createdAt(LocalDateTime.now())
                .build();

        return trainingPeriodRepository.save(period)
                .flatMap(saved -> saveAdditionalOffers(saved.getId(), primaryOfferId, offerIds).thenReturn(saved))
                .flatMap(this::toDetails);
    }

    @Override
    @Transactional
    public Mono<TrainingPeriodDetails> updateStatus(UUID schoolId, UUID periodId, String statusRaw) {
        TrainingPeriod.TrainingPeriodStatus status;
        try {
            status = TrainingPeriod.TrainingPeriodStatus.valueOf(statusRaw.trim().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Statut invalide"));
        }

        return trainingPeriodRepository.findById(periodId)
                .filter(tp -> schoolId.equals(tp.getSchoolId()))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Session introuvable")))
                .flatMap(tp -> {
                    tp.setStatus(status);
                    return trainingPeriodRepository.save(tp);
                })
                .flatMap(this::toDetails);
    }

    @Override
    @Transactional
    public Mono<Void> delete(UUID schoolId, UUID periodId) {
        return trainingPeriodRepository.findById(periodId)
                .filter(tp -> schoolId.equals(tp.getSchoolId()))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Session introuvable")))
                .flatMap(tp -> sessionOfferRepository.deleteByTrainingPeriodId(tp.getId()).then(trainingPeriodRepository.delete(tp)));
    }

    private UUID resolvePrimaryOfferId(UUID offerId, List<UUID> offerIds) {
        if (offerId != null) {
            return offerId;
        }
        if (offerIds != null && !offerIds.isEmpty()) {
            return offerIds.get(0);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Au moins une offre est requise");
    }

    private Mono<Void> saveAdditionalOffers(UUID periodId, UUID primaryOfferId, List<UUID> offerIds) {
        if (offerIds == null || offerIds.isEmpty()) {
            return Mono.empty();
        }

        return Flux.fromIterable(offerIds)
                .distinct()
                .filter(id -> !id.equals(primaryOfferId))
                .flatMap(id -> sessionOfferRepository.save(SessionOffer.builder()
                        .trainingPeriodId(periodId)
                        .offerId(id)
                        .createdAt(LocalDateTime.now())
                        .build()))
                .then();
    }

    private Mono<TrainingPeriodDetails> toDetails(TrainingPeriod period) {
        Mono<List<TrainingPeriodFormation>> formationsMono = Flux.concat(
                Mono.justOrEmpty(period.getOfferId()),
                sessionOfferRepository.findByTrainingPeriodId(period.getId()).map(SessionOffer::getOfferId))
                .distinct()
                .flatMap(offerRepository::findById)
                .map(offer -> TrainingPeriodFormation.builder()
                        .offerId(offer.getId())
                        .offerName(offer.getName())
                        .permitType(offer.getPermitType())
                        .price(offer.getPrice())
                        .build())
                .collectList()
                .defaultIfEmpty(new ArrayList<>());

        Mono<Integer> enrolledMono = enrollmentRepository.countByTrainingPeriodId(period.getId())
                .map(Long::intValue)
                .defaultIfEmpty(0);

        return Mono.zip(formationsMono, enrolledMono)
                .map(tuple -> TrainingPeriodDetails.builder()
                        .id(period.getId())
                        .name(period.getName())
                        .description(period.getDescription())
                        .startDate(period.getStartDate())
                        .endDate(period.getEndDate())
                        .enrollmentDeadline(period.getEnrollmentDeadline())
                        .maxStudents(period.getMaxStudents())
                        .status(period.getStatus() != null ? period.getStatus().name() : "DRAFT")
                        .totalEnrolled(tuple.getT2())
                        .formations(tuple.getT1())
                        .build());
    }
}
