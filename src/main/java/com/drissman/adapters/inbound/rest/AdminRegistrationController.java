package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.RegistrationDto;
import com.drissman.adapters.inbound.rest.dto.ReviewRegistrationRequest;
import com.drissman.adapters.inbound.rest.mapper.RegistrationRestMapper;
import com.drissman.domain.model.User;
import com.drissman.ports.inbound.RegistrationUseCase;
import com.drissman.ports.outbound.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/registrations")
@RequiredArgsConstructor
public class AdminRegistrationController {

    private final RegistrationUseCase registrationUseCase;
    private final UserRepositoryPort userRepository;

    @GetMapping
    public Flux<RegistrationDto> getRegistrations(Principal principal) {
        return getSchoolId(principal)
                .flatMapMany(schoolId -> registrationUseCase.getAdminRegistrations(schoolId))
                .map(RegistrationRestMapper::toDto);
    }

    @GetMapping("/{id}")
    public Mono<RegistrationDto> getRegistration(@PathVariable UUID id, Principal principal) {
        return getSchoolId(principal)
                .flatMap(schoolId -> registrationUseCase.getRegistration(id)
                        .filter(reg -> reg.getSchoolId().equals(schoolId))
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé")))
                ).map(RegistrationRestMapper::toDto);
    }

    @PutMapping("/{id}/approve")
    public Mono<RegistrationDto> approveRegistration(
            @PathVariable UUID id, 
            @RequestBody ReviewRegistrationRequest request, 
            Principal principal) {
        UUID adminId = UUID.fromString(principal.getName());
        return getSchoolId(principal)
                .flatMap(schoolId -> registrationUseCase.getRegistration(id)
                        .filter(reg -> reg.getSchoolId().equals(schoolId))
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé")))
                        .flatMap(reg -> registrationUseCase.approveRegistration(id, adminId, request.getRemarks(), request.getMonitorId()))
                ).map(RegistrationRestMapper::toDto);
    }

    @PutMapping("/{id}/reject")
    public Mono<RegistrationDto> rejectRegistration(
            @PathVariable UUID id, 
            @RequestBody ReviewRegistrationRequest request, 
            Principal principal) {
        UUID adminId = UUID.fromString(principal.getName());
        return getSchoolId(principal)
                .flatMap(schoolId -> registrationUseCase.getRegistration(id)
                        .filter(reg -> reg.getSchoolId().equals(schoolId))
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé")))
                        .flatMap(reg -> registrationUseCase.rejectRegistration(id, adminId, request.getRemarks()))
                ).map(RegistrationRestMapper::toDto);
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
