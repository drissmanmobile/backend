package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.*;
import com.drissman.adapters.inbound.rest.mapper.SessionRestMapper;
import com.drissman.adapters.inbound.rest.dto.ApiResponse;
import com.drissman.domain.model.User;
import com.drissman.ports.outbound.UserRepositoryPort;
import com.drissman.ports.inbound.SessionUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/schools/admin/sessions")
@RequiredArgsConstructor
public class AdminSessionController {

    private final SessionUseCase sessionUseCase;
    private final UserRepositoryPort userRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<SessionDto>> createSession(
            Principal principal,
            @Valid @RequestBody CreateSessionRequest request) {
        return getSchoolId(principal)
                .flatMap(schoolId -> sessionUseCase.scheduleSession(
                        schoolId,
                        request.getEnrollmentId(),
                        request.getOfferId(),
                        request.getMonitorId(),
                        request.getVehicleId(),
                        request.getModuleId(),
                        request.getLessonId(),
                        request.getDate(),
                        request.getStartTime(),
                        request.getEndTime(),
                        request.getMeetingPoint()
                ))
                .map(SessionRestMapper::toDto)
                .map(data -> ApiResponse.created(data, "Session planifiée avec succès"));
    }

    @GetMapping("/enrollment/{enrollmentId}")
    public Mono<ApiResponse<List<SessionDto>>> getSessionsByEnrollment(
            Principal principal,
            @PathVariable UUID enrollmentId) {
        return getSchoolId(principal)
                .flatMap(schoolId -> sessionUseCase.getSessionsForEnrollment(schoolId, enrollmentId)
                        .map(SessionRestMapper::toDto)
                        .collectList())
                .map(ApiResponse::ok);
    }

    @GetMapping("/monitor/{monitorId}")
    public Mono<ApiResponse<List<SessionDto>>> getSessionsByMonitor(
            Principal principal,
            @PathVariable UUID monitorId) {
        return getSchoolId(principal)
                .flatMap(schoolId -> sessionUseCase.getSessionsForMonitor(schoolId, monitorId)
                        .map(SessionRestMapper::toDto)
                        .collectList())
                .map(ApiResponse::ok);
    }

    @GetMapping("/available-offers")
    public Mono<ApiResponse<List<AvailableOfferDto>>> getAvailableOffers(
            Principal principal,
            @RequestParam LocalDate date) {
        return getSchoolId(principal)
                .flatMap(schoolId -> sessionUseCase.getAvailableOffersForDate(schoolId, date)
                        .map(SessionRestMapper::toAvailableOfferDto)
                        .collectList())
                .map(ApiResponse::ok);
    }

    @GetMapping("/available-enrollments")
    public Mono<ApiResponse<List<SessionEnrollmentOptionDto>>> getAvailableEnrollments(
            Principal principal,
            @RequestParam UUID offerId,
            @RequestParam LocalDate date) {
        return getSchoolId(principal)
                .flatMap(schoolId -> sessionUseCase.getAvailableEnrollments(schoolId, offerId, date)
                        .flatMap(enrollment -> userRepository.findById(enrollment.getUserId())
                                .map(user -> SessionRestMapper.toEnrollmentOptionDto(enrollment, user)))
                        .collectList()
                        .map(list -> {
                            list.sort((a, b) -> a.getStudentName().compareToIgnoreCase(b.getStudentName()));
                            return list;
                        }))
                .map(ApiResponse::ok);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> cancelSession(
            Principal principal,
            @PathVariable UUID id) {
        return getSchoolId(principal)
                .flatMap(schoolId -> sessionUseCase.cancelSession(schoolId, id));
    }

    @PatchMapping("/{id}/complete")
    public Mono<ApiResponse<SessionDto>> completeSession(
            Principal principal,
            @PathVariable UUID id,
            @RequestParam(required = false) String notes) {
        return getSchoolId(principal)
                .flatMap(schoolId -> sessionUseCase.completeSession(schoolId, id, notes))
                .map(SessionRestMapper::toDto)
                .map(data -> ApiResponse.ok(data, "Session complétée avec succès"));
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
}
