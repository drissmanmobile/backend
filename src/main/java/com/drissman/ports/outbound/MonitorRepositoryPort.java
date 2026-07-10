package com.drissman.ports.outbound;

import com.drissman.domain.model.Monitor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MonitorRepositoryPort {
    Mono<Monitor> findById(UUID id);
    Flux<Monitor> findBySchoolId(UUID schoolId);
    Flux<Monitor> findBySchoolIdAndStatus(UUID schoolId, Monitor.MonitorStatus status);
    Mono<Boolean> existsByLicenseNumber(String licenseNumber);
    Mono<Monitor> findByUserId(UUID userId);
    Mono<Monitor> save(Monitor monitor);
    Mono<Void> deleteById(UUID id);
}
