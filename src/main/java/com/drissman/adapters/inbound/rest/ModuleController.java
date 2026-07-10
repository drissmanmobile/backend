package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.CreateModuleRequest;
import com.drissman.adapters.inbound.rest.dto.ModuleDto;
import com.drissman.adapters.inbound.rest.mapper.ModuleRestMapper;
import com.drissman.domain.model.User;
import com.drissman.ports.inbound.ModuleUseCase;
import com.drissman.ports.outbound.UserRepositoryPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleUseCase moduleUseCase;
    private final UserRepositoryPort userRepository;

    @GetMapping
    public Flux<ModuleDto> getModules(Principal principal) {
        return getSchoolId(principal)
                .flatMapMany(schoolId -> moduleUseCase.getModules(schoolId)
                        .map(ModuleRestMapper::toDto));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ModuleDto> createModule(@Valid @RequestBody CreateModuleRequest request, Principal principal) {
        return getSchoolId(principal)
                .flatMap(schoolId -> moduleUseCase.createModule(
                        schoolId,
                        request.getName(),
                        request.getCategory(),
                        request.getDescription(),
                        request.getOrderIndex(),
                        request.getRequiredHours()
                ))
                .map(ModuleRestMapper::toDto);
    }

    @PutMapping("/{moduleId}")
    public Mono<ModuleDto> updateModule(@PathVariable UUID moduleId,
            @Valid @RequestBody CreateModuleRequest request,
            Principal principal) {
        return getSchoolId(principal)
                .flatMap(schoolId -> moduleUseCase.updateModule(
                        moduleId,
                        schoolId,
                        request.getName(),
                        request.getCategory(),
                        request.getDescription(),
                        request.getOrderIndex(),
                        request.getRequiredHours()
                ))
                .map(ModuleRestMapper::toDto);
    }

    @DeleteMapping("/{moduleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteModule(@PathVariable UUID moduleId, Principal principal) {
        return getSchoolId(principal)
                .flatMap(schoolId -> moduleUseCase.deleteModule(moduleId, schoolId));
    }

    private Mono<UUID> getSchoolId(Principal principal) {
        if (principal == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        }
        return userRepository.findById(UUID.fromString(principal.getName()))
                .map(User::getSchoolId)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Utilisateur non associé à une école")));
    }
}
