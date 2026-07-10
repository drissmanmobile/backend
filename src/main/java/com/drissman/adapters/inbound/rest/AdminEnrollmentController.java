package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.EnrollmentViewDto;
import com.drissman.adapters.inbound.rest.dto.UpdateEnrollmentStatusRequest;
import com.drissman.adapters.inbound.rest.mapper.EnrollmentRestMapper;
import com.drissman.adapters.inbound.rest.dto.ApiResponse;
import com.drissman.adapters.inbound.rest.dto.InvoiceViewDto;
import com.drissman.domain.model.Enrollment;
import com.drissman.domain.model.Offer;
import com.drissman.domain.model.School;
import com.drissman.domain.model.User;
import com.drissman.ports.inbound.EnrollmentUseCase;
import com.drissman.ports.outbound.OfferRepositoryPort;
import com.drissman.ports.outbound.SchoolRepositoryPort;
import com.drissman.ports.outbound.UserRepositoryPort;
import com.drissman.service.InvoiceQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/schools/admin")
@RequiredArgsConstructor
public class AdminEnrollmentController {

    private final EnrollmentUseCase enrollmentUseCase;
    private final InvoiceQueryService invoiceQueryService;
    private final UserRepositoryPort userRepository;
    private final OfferRepositoryPort offerRepositoryPort;
    private final SchoolRepositoryPort schoolRepositoryPort;

    @GetMapping("/enrollments")
    public Mono<ApiResponse<List<EnrollmentViewDto>>> getSchoolEnrollments(Principal principal) {
        return getSchoolId(principal)
                .flatMap(schoolId -> enrollmentUseCase.getEnrollmentsForSchool(schoolId)
                        .flatMap(this::enrich)
                        .collectList()
                        .map(list -> {
                            list.sort((a, b) -> b.getEnrolledAt().compareTo(a.getEnrolledAt()));
                            return ApiResponse.ok(list);
                        }));
    }

    @PatchMapping("/enrollments/{id}/status")
    public Mono<ApiResponse<EnrollmentViewDto>> updateEnrollmentStatus(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEnrollmentStatusRequest request) {
        return getSchoolId(principal)
                .flatMap(schoolId -> enrollmentUseCase.updateEnrollmentStatus(schoolId, id, request.getStatus())
                        .flatMap(this::enrich)
                        .map(data -> ApiResponse.ok(data, "Statut mis à jour avec succès")));
    }

    @GetMapping("/invoices")
    public Mono<ApiResponse<List<InvoiceViewDto>>> getSchoolInvoices(Principal principal) {
        return getSchoolId(principal)
                .flatMap(schoolId -> invoiceQueryService.getInvoicesForSchool(schoolId).collectList())
                .map(ApiResponse::ok);
    }

    private Mono<UUID> getSchoolId(Principal principal) {
        if (principal == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise"));
        }
        return userRepository.findById(UUID.fromString(principal.getName()))
                .map(User::getSchoolId)
                .filter(schoolId -> schoolId != null)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Compte non associé à une école")));
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
