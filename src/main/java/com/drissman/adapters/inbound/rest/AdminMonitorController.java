package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.CreateMonitorRequest;
import com.drissman.adapters.inbound.rest.dto.MonitorDto;
import com.drissman.adapters.inbound.rest.dto.UpdateMonitorRequest;
import com.drissman.adapters.inbound.rest.mapper.MonitorRestMapper;
import com.drissman.adapters.inbound.rest.dto.ApiResponse;
import com.drissman.domain.model.User;
import com.drissman.ports.outbound.UserRepositoryPort;
import com.drissman.ports.inbound.MonitorUseCase;
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
@RequestMapping("/api/schools/admin/monitors")
@RequiredArgsConstructor
public class AdminMonitorController {

    private final MonitorUseCase monitorUseCase;
    private final UserRepositoryPort userRepository;

    @GetMapping
    public Mono<ApiResponse<List<MonitorDto>>> getMonitors(Principal principal) {
        return getSchoolId(principal)
                .flatMap(schoolId -> monitorUseCase.getMonitorsBySchool(schoolId)
                        .map(MonitorRestMapper::toDto)
                        .collectList())
                .map(ApiResponse::ok);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<MonitorDto>> createMonitor(
            Principal principal,
            @Valid @RequestBody CreateMonitorRequest request) {
        return getSchoolId(principal)
                .flatMap(schoolId -> monitorUseCase.createMonitor(
                        schoolId,
                        request.getFirstName(),
                        request.getLastName(),
                        request.getEmail(),
                        request.getPassword(),
                        request.getLicenseNumber(),
                        request.getPhoneNumber()
                ))
                .map(MonitorRestMapper::toDto)
                .map(data -> ApiResponse.created(data, "Moniteur créé avec succès"));
    }

    @PatchMapping("/{id}")
    public Mono<ApiResponse<MonitorDto>> updateMonitor(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMonitorRequest request) {
        return getSchoolId(principal)
                .flatMap(schoolId -> monitorUseCase.updateMonitor(
                        schoolId,
                        id,
                        request.getFirstName(),
                        request.getLastName(),
                        request.getLicenseNumber(),
                        request.getPhoneNumber(),
                        request.getStatus()
                ))
                .map(MonitorRestMapper::toDto)
                .map(data -> ApiResponse.ok(data, "Moniteur mis à jour avec succès"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMonitor(Principal principal, @PathVariable UUID id) {
        return getSchoolId(principal)
                .flatMap(schoolId -> monitorUseCase.deleteMonitor(schoolId, id));
    }

    private Mono<UUID> getSchoolId(Principal principal) {
        if (principal == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise"));
        }
        return userRepository.findById(UUID.fromString(principal.getName()))
                .map(User::getSchoolId)
                .filter(schoolId -> schoolId != null)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Utilisateur non associé à une école")));
    }
}
