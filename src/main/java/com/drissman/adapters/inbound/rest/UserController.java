package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.ApiResponse;
import com.drissman.adapters.inbound.rest.dto.ChangePasswordRequest;
import com.drissman.adapters.inbound.rest.dto.UpdateProfileRequest;
import com.drissman.adapters.inbound.rest.dto.UserDto;
import com.drissman.adapters.inbound.rest.mapper.UserRestMapper;
import com.drissman.ports.inbound.UserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;

    @GetMapping("/me")
    public Mono<ApiResponse<UserDto>> getCurrentUser(Principal principal) {
        if (principal == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise"));
        }
        UUID userId = UUID.fromString(principal.getName());
        return userUseCase.findById(userId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé")))
                .map(UserRestMapper::toDto)
                .map(ApiResponse::ok);
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<UserDto>> getUser(Principal principal, @PathVariable UUID id) {
        if (principal == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise"));
        }
        UUID authenticatedUserId = UUID.fromString(principal.getName());
        if (!authenticatedUserId.equals(id)) {
            return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé"));
        }
        return userUseCase.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé")))
                .map(UserRestMapper::toDto)
                .map(ApiResponse::ok);
    }

    @PutMapping("/me")
    public Mono<ApiResponse<UserDto>> updateCurrentUser(
            Principal principal,
            @Valid @RequestBody UpdateProfileRequest request) {
        if (principal == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise"));
        }
        UUID authenticatedUserId = UUID.fromString(principal.getName());
        return userUseCase.updateProfile(authenticatedUserId, request.getFirstName(), request.getLastName(), request.getEmail(), request.getAvatarUrl())
                .map(UserRestMapper::toDto)
                .map(data -> ApiResponse.ok(data, "Profil mis à jour avec succès"));
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<UserDto>> updateProfile(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProfileRequest request) {
        if (principal == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise"));
        }
        UUID authenticatedUserId = UUID.fromString(principal.getName());
        if (!authenticatedUserId.equals(id)) {
            return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous ne pouvez modifier que votre propre profil"));
        }
        return userUseCase.updateProfile(id, request.getFirstName(), request.getLastName(), request.getEmail(), request.getAvatarUrl())
                .map(UserRestMapper::toDto)
                .map(data -> ApiResponse.ok(data, "Profil mis à jour avec succès"));
    }

    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> changePassword(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody ChangePasswordRequest request) {
        if (principal == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise"));
        }
        UUID authenticatedUserId = UUID.fromString(principal.getName());
        if (!authenticatedUserId.equals(id)) {
            return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous ne pouvez modifier que votre propre mot de passe"));
        }
        return userUseCase.changePassword(id, request.getCurrentPassword(), request.getNewPassword());
    }
}
