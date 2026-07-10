package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.CreateRegistrationRequest;
import com.drissman.adapters.inbound.rest.dto.RegistrationDto;
import com.drissman.adapters.inbound.rest.mapper.RegistrationRestMapper;
import com.drissman.ports.inbound.RegistrationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationUseCase registrationUseCase;

    @PostMapping
    public Mono<RegistrationDto> createRegistration(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateRegistrationRequest request) {
        UUID userId = UUID.fromString(userDetails.getUsername()); // Assuming username is the ID here based on JwtTokenProvider
        
        return registrationUseCase.createRegistration(
                userId,
                request.getSchoolId(),
                request.getFormationId(),
                request.getCategory(),
                request.getRemarks(),
                request.getPhone(),
                request.getCniNumber(),
                request.getBirthDate(),
                request.getAddress()
        ).map(RegistrationRestMapper::toDto);
    }

    @GetMapping("/me")
    public Flux<RegistrationDto> getMyRegistrations(@AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        return registrationUseCase.getMyRegistrations(userId)
                .map(RegistrationRestMapper::toDto);
    }
}
