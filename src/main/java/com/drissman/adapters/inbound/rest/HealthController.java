package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public Mono<ApiResponse<Map<String, Object>>> health() {
        return Mono.just(ApiResponse.ok(Map.of(
                "status", "UP",
                "service", "drissman-backend",
                "timestamp", LocalDateTime.now().toString()
        )));
    }
}
