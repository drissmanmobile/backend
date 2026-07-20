package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.ApiResponse;
import com.drissman.adapters.inbound.rest.dto.VehicleDto;
import com.drissman.adapters.inbound.rest.mapper.VehicleRestMapper;
import com.drissman.domain.model.User;
import com.drissman.domain.model.Vehicle;
import com.drissman.ports.inbound.VehicleUseCase;
import com.drissman.ports.outbound.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/schools/admin/vehicles")
@RequiredArgsConstructor
public class AdminVehicleController {

    private final VehicleUseCase vehicleUseCase;
    private final UserRepositoryPort userRepository;

    @GetMapping
    public Mono<ApiResponse<List<VehicleDto>>> getVehicles(Principal principal) {
        return getSchoolId(principal)
                .flatMap(schoolId -> vehicleUseCase.getVehiclesBySchool(schoolId)
                        .map(VehicleRestMapper::toDto)
                        .collectList())
                .map(ApiResponse::ok);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<VehicleDto>> createVehicle(Principal principal, @RequestBody VehicleDto request) {
        return getSchoolId(principal)
                .flatMap(schoolId -> {
                    Vehicle vehicle = VehicleRestMapper.toDomain(request);
                    vehicle.setSchoolId(schoolId);
                    return vehicleUseCase.createVehicle(vehicle);
                })
                .map(VehicleRestMapper::toDto)
                .map(data -> ApiResponse.created(data, "Véhicule créé avec succès"));
    }

    @PatchMapping("/{id}")
    public Mono<ApiResponse<VehicleDto>> updateVehicle(Principal principal, @PathVariable UUID id, @RequestBody VehicleDto request) {
        return getSchoolId(principal)
                .flatMap(schoolId -> vehicleUseCase.getVehicle(id)
                        .filter(v -> v.getSchoolId().equals(schoolId))
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Véhicule non trouvé")))
                        .flatMap(v -> vehicleUseCase.updateVehicle(id, VehicleRestMapper.toDomain(request)))
                )
                .map(VehicleRestMapper::toDto)
                .map(data -> ApiResponse.ok(data, "Véhicule mis à jour avec succès"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteVehicle(Principal principal, @PathVariable UUID id) {
        return getSchoolId(principal)
                .flatMap(schoolId -> vehicleUseCase.getVehicle(id)
                        .filter(v -> v.getSchoolId().equals(schoolId))
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Véhicule non trouvé")))
                        .flatMap(v -> vehicleUseCase.deleteVehicle(id))
                );
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
