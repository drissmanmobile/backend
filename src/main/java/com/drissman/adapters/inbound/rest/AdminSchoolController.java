package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.ApiResponse;
import com.drissman.adapters.inbound.rest.dto.AdminDashboardDto;
import com.drissman.adapters.inbound.rest.dto.PartnerStatsDto;
import com.drissman.adapters.inbound.rest.dto.UpdateSchoolRequest;
import com.drissman.ports.outbound.UserRepositoryPort;
import com.drissman.service.AdminSchoolService;
import com.drissman.ports.inbound.SchoolUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/schools/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminSchoolController {

    private final AdminSchoolService adminSchoolService;
    private final SchoolUseCase schoolUseCase;
    private final UserRepositoryPort userRepository;

    @GetMapping("/dashboard")
    public Mono<ApiResponse<AdminDashboardDto>> getDashboardStats(Principal principal) {
        if (principal == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise"));
        }
        UUID userId = UUID.fromString(principal.getName());
        return userRepository.findById(userId)
                .flatMap(user -> {
                    if (user.getSchoolId() == null) {
                        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Compte non associé à une école"));
                    }
                    return adminSchoolService.getDashboardStats(user.getSchoolId());
                })
                .map(ApiResponse::ok);
    }

    @GetMapping("/stats")
    public Mono<ApiResponse<PartnerStatsDto>> getStats(Principal principal) {
        if (principal == null) {
            log.info("Demo mode: returning mock stats");
            return Mono.just(ApiResponse.ok(PartnerStatsDto.builder()
                    .revenue("2,450,000 FCFA")
                    .enrollments(127)
                    .successRate("94%")
                    .upcomingLessons(23)
                    .revenueGrowth(12.0)
                    .enrollmentGrowth(8)
                    .build()));
        }

        log.info("Fetching stats for user: {}", principal.getName());
        UUID userId = UUID.fromString(principal.getName());

        return userRepository.findById(userId)
                .flatMap(user -> {
                    if (user.getSchoolId() == null) {
                        return Mono.just(PartnerStatsDto.builder()
                                .revenue("0 FCFA")
                                .enrollments(0)
                                .successRate("0%")
                                .upcomingLessons(0)
                                .revenueGrowth(0.0)
                                .enrollmentGrowth(0)
                                .build());
                    }
                    return adminSchoolService.getStats(user.getSchoolId());
                })
                .map(ApiResponse::ok)
                .switchIfEmpty(Mono.error(new RuntimeException("Utilisateur non trouvé")));
    }

    @GetMapping("/profile")
    public Mono<ApiResponse<com.drissman.domain.model.School>> getProfile(Principal principal) {
        if (principal == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise"));
        }
        UUID userId = UUID.fromString(principal.getName());
        return userRepository.findById(userId)
                .flatMap(user -> {
                    if (user.getSchoolId() == null) {
                        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Compte non associé à une école"));
                    }
                    return schoolUseCase.findById(user.getSchoolId());
                })
                .map(ApiResponse::ok)
                .switchIfEmpty(Mono.error(new RuntimeException("École introuvable")));
    }

    @PatchMapping
    public Mono<ApiResponse<Void>> updateSchool(Principal principal, @RequestBody UpdateSchoolRequest request) {
        if (principal == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise"));
        }
        UUID userId = UUID.fromString(principal.getName());
        return userRepository.findById(userId)
                .flatMap(user -> {
                    if (user.getSchoolId() == null) {
                        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Compte non associé à une école"));
                    }
                    return schoolUseCase.update(
                            user.getSchoolId(), 
                            request.getName(), 
                            request.getDescription(), 
                            request.getAddress(),
                            request.getCity(),
                            request.getRegion(),
                            request.getPhone(),
                            request.getEmail(),
                            request.getWebsite(),
                            request.getImageUrl()
                    ).then();
                })
                .thenReturn(ApiResponse.<Void>ok(null, "École mise à jour avec succès"));
    }
}
