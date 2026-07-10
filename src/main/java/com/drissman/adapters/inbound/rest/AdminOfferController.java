package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.SchoolDto;
import com.drissman.adapters.inbound.rest.mapper.OfferRestMapper;
import com.drissman.adapters.inbound.rest.dto.ApiResponse;
import com.drissman.adapters.inbound.rest.dto.CreateOfferRequest;
import com.drissman.adapters.inbound.rest.dto.UpdateOfferRequest;
import com.drissman.adapters.inbound.rest.dto.OfferModuleDto;
import com.drissman.adapters.inbound.rest.dto.SetOfferModulesRequest;
import com.drissman.domain.model.User;
import com.drissman.ports.outbound.UserRepositoryPort;
import com.drissman.ports.inbound.OfferUseCase;
import com.drissman.ports.inbound.OfferModuleUseCase;
import com.drissman.adapters.inbound.rest.mapper.OfferModuleRestMapper;
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
@RequestMapping("/api/schools/admin/offers")
@RequiredArgsConstructor
public class AdminOfferController {

    private final OfferUseCase offerUseCase;
    private final OfferModuleUseCase offerModuleUseCase;
    private final UserRepositoryPort userRepository;

    @GetMapping
    public Mono<ApiResponse<List<SchoolDto.OfferDto>>> getOffers(Principal principal) {
        return getSchoolId(principal)
                .flatMap(schoolId -> offerUseCase.findBySchoolId(schoolId)
                        .map(OfferRestMapper::toDto)
                        .collectList())
                .map(ApiResponse::ok);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<SchoolDto.OfferDto>> create(
            Principal principal,
            @Valid @RequestBody CreateOfferRequest request) {
        return getSchoolId(principal)
                .flatMap(schoolId -> offerUseCase.create(
                        schoolId,
                        request.getName(),
                        request.getDescription(),
                        request.getPrice(),
                        request.getHours(),
                        request.getPermitType()
                ))
                .map(OfferRestMapper::toDto)
                .map(data -> ApiResponse.created(data, "Offre créée avec succès"));
    }

    @PatchMapping("/{id}")
    public Mono<ApiResponse<SchoolDto.OfferDto>> update(
            Principal principal,
            @PathVariable UUID id,
            @RequestBody UpdateOfferRequest request) {
        return getSchoolId(principal)
                .flatMap(schoolId -> offerUseCase.update(
                        schoolId,
                        id,
                        request.getName(),
                        request.getDescription(),
                        request.getPrice(),
                        request.getHours(),
                        request.getPermitType()
                ))
                .map(OfferRestMapper::toDto)
                .map(data -> ApiResponse.ok(data, "Offre mise à jour avec succès"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(Principal principal, @PathVariable UUID id) {
        return getSchoolId(principal)
                .flatMap(schoolId -> offerUseCase.delete(schoolId, id));
    }

    @PutMapping("/{offerId}/modules")
    public Mono<ApiResponse<List<OfferModuleDto>>> setModules(
            Principal principal,
            @PathVariable UUID offerId,
            @RequestBody SetOfferModulesRequest request) {
        return getSchoolId(principal)
                .flatMap(schoolId -> {
                    var useCaseModules = request.getModules().stream()
                            .map(OfferModuleRestMapper::toUseCaseEntry)
                            .toList();
                    return offerModuleUseCase.setModulesForOffer(schoolId, offerId, useCaseModules)
                            .map(OfferModuleRestMapper::toDto)
                            .collectList();
                })
                .map(ApiResponse::ok);
    }

    @DeleteMapping("/{offerId}/modules/{moduleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> removeModule(
            Principal principal,
            @PathVariable UUID offerId,
            @PathVariable UUID moduleId) {
        return getSchoolId(principal)
                .flatMap(schoolId -> offerModuleUseCase.removeModuleFromOffer(schoolId, offerId, moduleId));
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
