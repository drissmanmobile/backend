package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.ApiResponse;
import com.drissman.adapters.inbound.rest.dto.AuthResponse;
import com.drissman.adapters.inbound.rest.dto.LoginRequest;
import com.drissman.adapters.inbound.rest.dto.RegisterRequest;
import com.drissman.adapters.inbound.rest.dto.GoogleLoginRequest;
import com.drissman.adapters.inbound.rest.dto.VerifyEmailRequest;
import com.drissman.adapters.inbound.rest.dto.ResendOtpRequest;
import com.drissman.adapters.inbound.rest.dto.UpgradeVisitorRoleRequest;
import com.drissman.ports.inbound.ResendVerificationUseCase;
import com.drissman.ports.inbound.VerifyEmailUseCase;
import com.drissman.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final VerifyEmailUseCase verifyEmailUseCase;
    private final ResendVerificationUseCase resendVerificationUseCase;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request)
                .map(data -> ApiResponse.created(data, "Compte créé avec succès"));
    }

    @PostMapping("/login")
    public Mono<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request)
                .map(data -> ApiResponse.ok(data, "Connexion réussie"));
    }

    @PostMapping("/google")
    public Mono<ApiResponse<AuthResponse>> loginWithGoogle(@Valid @RequestBody GoogleLoginRequest request) {
        return authService.loginWithGoogle(request)
                .map(data -> ApiResponse.ok(data, "Connexion avec Google réussie"));
    }

    @PostMapping("/verify-email")
    public Mono<ApiResponse<Void>> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        return verifyEmailUseCase.verifyEmail(request.getEmail(), request.getCode())
                .then(Mono.just(ApiResponse.ok(null, "Email vérifié avec succès")));
    }

    @PostMapping("/resend-otp")
    public Mono<ApiResponse<Void>> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        return resendVerificationUseCase.resendVerification(request.getEmail())
                .then(Mono.just(ApiResponse.ok(null, "Code renvoyé avec succès")));
    }

    @PostMapping("/upgrade-visitor")
    public Mono<ApiResponse<AuthResponse>> upgradeVisitor(
            Principal principal,
            @Valid @RequestBody UpgradeVisitorRoleRequest request) {
        if (principal == null) {
            return Mono.error(new RuntimeException("Authentification requise"));
        }
        return authService.upgradeVisitorRole(UUID.fromString(principal.getName()), request)
                .map(data -> ApiResponse.ok(data, "Rôle mis à jour avec succès"));
    }
}
