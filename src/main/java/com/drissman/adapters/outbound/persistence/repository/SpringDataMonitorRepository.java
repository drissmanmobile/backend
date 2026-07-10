package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.MonitorEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SpringDataMonitorRepository extends ReactiveCrudRepository<MonitorEntity, UUID> {

    Flux<MonitorEntity> findBySchoolId(UUID schoolId);

    Flux<MonitorEntity> findBySchoolIdAndStatus(UUID schoolId, String status);

    Mono<Boolean> existsByLicenseNumber(String licenseNumber);

    Mono<MonitorEntity> findByUserId(UUID userId);
}
