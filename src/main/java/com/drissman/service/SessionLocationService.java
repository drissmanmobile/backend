package com.drissman.service;

import com.drissman.adapters.inbound.rest.dto.RecordSessionLocationRequest;
import com.drissman.adapters.inbound.rest.dto.SessionLocationDto;
import com.drissman.adapters.outbound.persistence.entity.SessionLocationEntity;
import com.drissman.adapters.outbound.persistence.repository.SpringDataSessionLocationRepository;
import com.drissman.ports.outbound.SessionRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionLocationService {

    private final SpringDataSessionLocationRepository locationRepository;
    private final SessionRepositoryPort sessionRepository;

    public Mono<SessionLocationDto> recordLocation(UUID sessionId, RecordSessionLocationRequest request) {
        return sessionRepository.findById(sessionId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Séance non trouvée avec l'ID: " + sessionId)))
                .flatMap(session -> {
                    SessionLocationEntity entity = SessionLocationEntity.builder()
                            .id(UUID.randomUUID())
                            .sessionId(sessionId)
                            .vehicleId(session.getVehicleId())
                            .latitude(request.getLatitude())
                            .longitude(request.getLongitude())
                            .speed(request.getSpeed())
                            .heading(request.getHeading())
                            .createdAt(LocalDateTime.now())
                            .build();

                    return locationRepository.save(entity)
                            .doOnSuccess(saved -> log.debug("Enregistrement position séance {}: lat={}, lng={}", sessionId, request.getLatitude(), request.getLongitude()))
                            .map(this::toDto);
                });
    }

    public Mono<SessionLocationDto> getLatestLocation(UUID sessionId) {
        return locationRepository.findTopBySessionIdOrderByCreatedAtDesc(sessionId)
                .map(this::toDto);
    }

    public Flux<SessionLocationDto> getSessionTrail(UUID sessionId) {
        return locationRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)
                .map(this::toDto);
    }

    private SessionLocationDto toDto(SessionLocationEntity entity) {
        return SessionLocationDto.builder()
                .id(entity.getId())
                .sessionId(entity.getSessionId())
                .vehicleId(entity.getVehicleId())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .speed(entity.getSpeed())
                .heading(entity.getHeading())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
