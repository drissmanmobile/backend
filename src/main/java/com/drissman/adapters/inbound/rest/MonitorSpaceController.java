package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.MonitorDto;
import com.drissman.adapters.inbound.rest.dto.MonitorSessionViewDto;
import com.drissman.adapters.inbound.rest.dto.MonitorStudentProgressDto;
import com.drissman.adapters.inbound.rest.dto.SessionDto;
import com.drissman.adapters.inbound.rest.mapper.MonitorRestMapper;
import com.drissman.adapters.inbound.rest.mapper.SessionRestMapper;
import com.drissman.adapters.inbound.rest.dto.ApiResponse;
import com.drissman.domain.model.Offer;
import com.drissman.domain.model.User;
import com.drissman.ports.outbound.UserRepositoryPort;
import com.drissman.ports.inbound.MonitorUseCase;
import com.drissman.ports.inbound.SessionUseCase;
import com.drissman.ports.outbound.EnrollmentRepositoryPort;
import com.drissman.ports.outbound.OfferRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/monitors")
@RequiredArgsConstructor
public class MonitorSpaceController {

    private final MonitorUseCase monitorUseCase;
    private final SessionUseCase sessionUseCase;
    private final EnrollmentRepositoryPort enrollmentRepositoryPort;
    private final UserRepositoryPort userRepository;
    private final OfferRepositoryPort offerRepositoryPort;

    @GetMapping("/me")
    public Mono<ApiResponse<MonitorDto>> getCurrentMonitorProfile(Principal principal) {
        if (principal == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise"));
        }
        UUID userId = UUID.fromString(principal.getName());
        return monitorUseCase.getMonitorByUserId(userId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Profil moniteur non trouvé pour cet utilisateur")))
                .map(MonitorRestMapper::toDto)
                .map(ApiResponse::ok);
    }

    @GetMapping("/me/sessions")
    public Mono<ApiResponse<List<MonitorSessionViewDto>>> getMySessions(Principal principal) {
        if (principal == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise"));
        }
        UUID userId = UUID.fromString(principal.getName());
        return sessionUseCase.getMonitorSessionsByUserId(userId)
                .flatMap(session -> enrollmentRepositoryPort.findById(session.getEnrollmentId())
                        .flatMap(enrollment -> {
                            Mono<User> userMono = userRepository.findById(enrollment.getUserId());
                            Mono<Offer> offerMono = offerRepositoryPort.findById(enrollment.getOfferId());
                            return Mono.zip(userMono, offerMono)
                                    .map(tuple -> SessionRestMapper.toMonitorSessionView(session, enrollment, tuple.getT1(), tuple.getT2()));
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

    @PatchMapping("/me/sessions/{id}/complete")
    public Mono<ApiResponse<SessionDto>> completeMySession(
            Principal principal,
            @PathVariable UUID id,
            @RequestParam(required = false) String notes) {
        if (principal == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise"));
        }
        UUID userId = UUID.fromString(principal.getName());
        return sessionUseCase.completeSessionByMonitor(userId, id, notes)
                .map(SessionRestMapper::toDto)
                .map(data -> ApiResponse.ok(data, "Session complétée avec succès"));
    }

    @GetMapping("/me/students")
    public Mono<ApiResponse<List<MonitorStudentProgressDto>>> getMyStudents(Principal principal) {
        if (principal == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise"));
        }
        UUID userId = UUID.fromString(principal.getName());
        return sessionUseCase.getStudentsForMonitor(userId)
                .flatMap(enrollment -> {
                    Mono<User> userMono = userRepository.findById(enrollment.getUserId());
                    Mono<Offer> offerMono = offerRepositoryPort.findById(enrollment.getOfferId());
                    return Mono.zip(userMono, offerMono)
                            .map(tuple -> SessionRestMapper.toMonitorStudentProgress(enrollment, tuple.getT1(), tuple.getT2()));
                })
                .collectList()
                .map(ApiResponse::ok);
    }
}
