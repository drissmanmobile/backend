package com.drissman.adapters.outbound.persistence;

import com.drissman.adapters.outbound.persistence.mapper.MonitorPersistenceMapper;
import com.drissman.adapters.outbound.persistence.repository.SpringDataMonitorRepository;
import com.drissman.domain.model.Monitor;
import com.drissman.ports.outbound.MonitorRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MonitorPersistenceAdapter implements MonitorRepositoryPort {

    private final SpringDataMonitorRepository springDataMonitorRepository;

    @Override
    public Mono<Monitor> findById(UUID id) {
        return springDataMonitorRepository.findById(id)
                .map(MonitorPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Monitor> findBySchoolId(UUID schoolId) {
        return springDataMonitorRepository.findBySchoolId(schoolId)
                .map(MonitorPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Monitor> findBySchoolIdAndStatus(UUID schoolId, Monitor.MonitorStatus status) {
        return springDataMonitorRepository.findBySchoolIdAndStatus(schoolId, status != null ? status.name() : null)
                .map(MonitorPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByLicenseNumber(String licenseNumber) {
        return springDataMonitorRepository.existsByLicenseNumber(licenseNumber);
    }

    @Override
    public Mono<Monitor> findByUserId(UUID userId) {
        return springDataMonitorRepository.findByUserId(userId)
                .map(MonitorPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Monitor> save(Monitor monitor) {
        return springDataMonitorRepository.save(MonitorPersistenceMapper.toEntity(monitor))
                .map(MonitorPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return springDataMonitorRepository.deleteById(id);
    }
}
