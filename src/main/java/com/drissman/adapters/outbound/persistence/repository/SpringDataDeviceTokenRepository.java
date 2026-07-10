package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.DeviceTokenEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface SpringDataDeviceTokenRepository extends R2dbcRepository<DeviceTokenEntity, UUID> {
    Flux<DeviceTokenEntity> findByUserId(UUID userId);
    Mono<DeviceTokenEntity> findByToken(String token);
    Mono<Void> deleteByToken(String token);
}
