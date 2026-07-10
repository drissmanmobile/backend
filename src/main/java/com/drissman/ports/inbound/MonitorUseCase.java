package com.drissman.ports.inbound;

import com.drissman.domain.model.Monitor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MonitorUseCase {
    Mono<Monitor> createMonitor(UUID schoolId, String firstName, String lastName, String email, String password, String licenseNumber, String phoneNumber);
    Flux<Monitor> getMonitorsBySchool(UUID schoolId);
    Mono<Monitor> getMonitorById(UUID monitorId);
    Mono<Monitor> getMonitorByUserId(UUID userId);
    Mono<Monitor> updateMonitor(UUID schoolId, UUID monitorId, String firstName, String lastName, String licenseNumber, String phoneNumber, Monitor.MonitorStatus status);
    Mono<Void> deleteMonitor(UUID schoolId, UUID monitorId);
}
