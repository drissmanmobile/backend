package com.drissman.adapters.outbound.persistence;

import com.drissman.adapters.outbound.persistence.mapper.SessionPersistenceMapper;
import com.drissman.adapters.outbound.persistence.repository.SpringDataSessionRepository;
import com.drissman.domain.model.Session;
import com.drissman.ports.outbound.SessionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SessionPersistenceAdapter implements SessionRepositoryPort {

    private final SpringDataSessionRepository springDataSessionRepository;

    @Override
    public Mono<Session> findById(UUID id) {
        return springDataSessionRepository.findById(id)
                .map(SessionPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Session> findByEnrollmentId(UUID enrollmentId) {
        return springDataSessionRepository.findByEnrollmentId(enrollmentId)
                .map(SessionPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Session> findByMonitorId(UUID monitorId) {
        return springDataSessionRepository.findByMonitorId(monitorId)
                .map(SessionPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Session> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        return springDataSessionRepository.findByDateBetween(startDate, endDate)
                .map(SessionPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Session> findBySchoolId(UUID schoolId) {
        return springDataSessionRepository.findBySchoolId(schoolId)
                .map(SessionPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Session> save(Session session) {
        return springDataSessionRepository.save(SessionPersistenceMapper.toEntity(session))
                .map(SessionPersistenceMapper::toDomain);
    }
}
