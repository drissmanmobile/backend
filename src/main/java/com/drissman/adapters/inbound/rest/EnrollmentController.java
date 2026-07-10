package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.CreateEnrollmentRequest;
import com.drissman.adapters.inbound.rest.dto.EnrollmentViewDto;
import com.drissman.adapters.inbound.rest.dto.CandidateSessionViewDto;
import com.drissman.adapters.inbound.rest.mapper.EnrollmentRestMapper;
import com.drissman.adapters.inbound.rest.mapper.SessionRestMapper;
import com.drissman.adapters.inbound.rest.dto.ApiResponse;
import com.drissman.domain.model.Enrollment;
import com.drissman.domain.model.Offer;
import com.drissman.domain.model.School;
import com.drissman.domain.model.User;
import com.drissman.ports.outbound.UserRepositoryPort;
import com.drissman.ports.inbound.EnrollmentUseCase;
import com.drissman.ports.inbound.SessionUseCase;
import com.drissman.ports.outbound.EnrollmentRepositoryPort;
import com.drissman.ports.outbound.MonitorRepositoryPort;
import com.drissman.ports.outbound.OfferRepositoryPort;
import com.drissman.ports.outbound.SchoolRepositoryPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentUseCase enrollmentUseCase;
    private final SessionUseCase sessionUseCase;
    private final EnrollmentRepositoryPort enrollmentRepositoryPort;
    private final OfferRepositoryPort offerRepositoryPort;
    private final MonitorRepositoryPort monitorRepositoryPort;
    private final UserRepositoryPort userRepository;
    private final SchoolRepositoryPort schoolRepositoryPort;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<EnrollmentViewDto>> createEnrollment(
            Principal principal,
            @Valid @RequestBody CreateEnrollmentRequest request) {
        if (principal == null) {
            return Mono.error(new RuntimeException("Authentification requise"));
        }
        return enrollmentUseCase.createEnrollment(UUID.fromString(principal.getName()), request.getOfferId())
                .flatMap(this::enrich)
                .map(data -> ApiResponse.created(data, "Inscription créée avec succès"));
    }

    @GetMapping("/me")
    public Mono<ApiResponse<List<EnrollmentViewDto>>> getMyEnrollments(Principal principal) {
        if (principal == null) {
            return Mono.error(new RuntimeException("Authentification requise"));
        }
        return enrollmentUseCase.getEnrollmentsForStudent(UUID.fromString(principal.getName()))
                .flatMap(this::enrich)
                .collectList()
                .map(list -> {
                    // Sort descending by enrolledAt
                    list.sort((a, b) -> b.getEnrolledAt().compareTo(a.getEnrolledAt()));
                    return ApiResponse.ok(list);
                });
    }

    @GetMapping("/me/sessions")
    public Mono<ApiResponse<List<CandidateSessionViewDto>>> getMySessions(Principal principal) {
        if (principal == null) {
            return Mono.error(new RuntimeException("Authentification requise"));
        }
        UUID userId = UUID.fromString(principal.getName());
        return sessionUseCase.getSessionsForStudent(userId)
                .flatMap(session -> enrollmentRepositoryPort.findById(session.getEnrollmentId())
                        .flatMap(enrollment -> {
                            Mono<Offer> offerMono = offerRepositoryPort.findById(enrollment.getOfferId());

                            Mono<String> monitorNameMono = Mono.just("Non assigne");
                            if (session.getMonitorId() != null) {
                                monitorNameMono = monitorRepositoryPort.findById(session.getMonitorId())
                                        .flatMap(monitor -> monitor.getUserId() != null
                                                ? userRepository.findById(monitor.getUserId())
                                                        .map(user -> ((user.getFirstName() != null ? user.getFirstName() : "") + " " + (user.getLastName() != null ? user.getLastName() : "")).trim())
                                                : Mono.just(((monitor.getFirstName() != null ? monitor.getFirstName() : "") + " " + (monitor.getLastName() != null ? monitor.getLastName() : "")).trim()))
                                        .defaultIfEmpty("Non assigne");
                            }

                            return Mono.zip(offerMono, monitorNameMono)
                                    .map(tuple -> SessionRestMapper.toCandidateSessionView(session, enrollment, tuple.getT1(), tuple.getT2()));
                        }))
                .collectList()
                .map(list -> {
                    list.sort((a, b) -> {
                        int byDate = b.getDate().compareTo(a.getDate());
                        if (byDate != 0) {
                            return byDate;
                        }
                        return b.getStartTime().compareTo(a.getStartTime());
                    });
                    return list;
                })
                .map(ApiResponse::ok);
    }

    private Mono<EnrollmentViewDto> enrich(Enrollment enrollment) {
        Mono<Offer> offerMono = offerRepositoryPort.findById(enrollment.getOfferId())
                .defaultIfEmpty(Offer.builder()
                        .id(enrollment.getOfferId())
                        .schoolId(enrollment.getSchoolId())
                        .name("Offre")
                        .price(0)
                        .hours(enrollment.getHoursPurchased())
                        .permitType("B")
                        .build());

        Mono<User> userMono = userRepository.findById(enrollment.getUserId())
                .defaultIfEmpty(User.builder()
                        .id(enrollment.getUserId())
                        .firstName("Eleve")
                        .lastName("Inconnu")
                        .build());

        Mono<School> schoolMono = schoolRepositoryPort.findById(enrollment.getSchoolId())
                .defaultIfEmpty(School.builder()
                        .id(enrollment.getSchoolId())
                        .name("Auto-ecole")
                        .build());

        return Mono.zip(offerMono, userMono, schoolMono)
                .map(tuple -> EnrollmentRestMapper.toViewDto(enrollment, tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }
}
