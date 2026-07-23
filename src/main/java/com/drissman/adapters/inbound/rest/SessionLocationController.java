package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.ApiResponse;
import com.drissman.adapters.inbound.rest.dto.RecordSessionLocationRequest;
import com.drissman.adapters.inbound.rest.dto.SessionLocationDto;
import com.drissman.service.SessionLocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sessions/{sessionId}/locations")
@RequiredArgsConstructor
public class SessionLocationController {

    private final SessionLocationService sessionLocationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<SessionLocationDto>> recordLocation(
            @PathVariable UUID sessionId,
            @Valid @RequestBody RecordSessionLocationRequest request) {
        return sessionLocationService.recordLocation(sessionId, request)
                .map(data -> ApiResponse.created(data, "Position de séance enregistrée avec succès"));
    }

    @GetMapping("/latest")
    public Mono<ApiResponse<SessionLocationDto>> getLatestLocation(@PathVariable UUID sessionId) {
        return sessionLocationService.getLatestLocation(sessionId)
                .map(ApiResponse::ok)
                .switchIfEmpty(Mono.just(ApiResponse.ok(null, "Aucune position disponible pour cette séance")));
    }

    @GetMapping("/trail")
    public Mono<ApiResponse<List<SessionLocationDto>>> getSessionTrail(@PathVariable UUID sessionId) {
        return sessionLocationService.getSessionTrail(sessionId)
                .collectList()
                .map(ApiResponse::ok);
    }
}
