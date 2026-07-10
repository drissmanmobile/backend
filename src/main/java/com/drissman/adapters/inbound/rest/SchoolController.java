package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.ApiResponse;
import com.drissman.adapters.inbound.rest.dto.SchoolDto;
import com.drissman.adapters.inbound.rest.mapper.SchoolRestMapper;
import com.drissman.ports.inbound.SchoolUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import com.drissman.ports.inbound.MonitorUseCase;
import com.drissman.adapters.inbound.rest.dto.MonitorDto;
import com.drissman.adapters.inbound.rest.mapper.MonitorRestMapper;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/schools")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolUseCase schoolUseCase;
    private final MonitorUseCase monitorUseCase;

    @GetMapping("/{schoolId}/monitors")
    public Mono<ApiResponse<List<MonitorDto>>> getMonitorsBySchool(@PathVariable UUID schoolId) {
        return monitorUseCase.getMonitorsBySchool(schoolId)
                .map(MonitorRestMapper::toDto)
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping
    public Mono<ApiResponse<List<SchoolDto>>> getAll(@RequestParam(required = false) String city) {
        return schoolUseCase.findAll(city)
                .map(SchoolRestMapper::toDto)
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/nearby")
    public Mono<ApiResponse<List<SchoolDto>>> getNearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "10") double radius) {
        return schoolUseCase.findNearby(lat, lng, radius)
                .map(SchoolRestMapper::toDto)
                .collectList()
                .map(ApiResponse::ok);
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<SchoolDto>> getById(@PathVariable UUID id) {
        return schoolUseCase.findById(id)
                .map(SchoolRestMapper::toDto)
                .map(ApiResponse::ok)
                .switchIfEmpty(Mono.error(new RuntimeException("École non trouvée")));
    }
}
