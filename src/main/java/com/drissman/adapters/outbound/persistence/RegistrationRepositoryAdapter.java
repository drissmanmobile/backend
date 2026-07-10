package com.drissman.adapters.outbound.persistence;

import com.drissman.adapters.outbound.persistence.mapper.RegistrationMapper;
import com.drissman.domain.model.Registration;
import com.drissman.ports.outbound.RegistrationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrationRepositoryAdapter implements RegistrationRepositoryPort {

    private final RegistrationDao registrationDao;

    @Override
    public Mono<Registration> save(Registration registration) {
        return registrationDao.save(RegistrationMapper.toEntity(registration))
                .map(RegistrationMapper::toDomain);
    }

    @Override
    public Mono<Registration> findById(UUID id) {
        return registrationDao.findById(id)
                .map(RegistrationMapper::toDomain);
    }

    @Override
    public Flux<Registration> findAllByUserId(UUID userId) {
        return registrationDao.findAllByUserId(userId)
                .map(RegistrationMapper::toDomain);
    }

    @Override
    public Flux<Registration> findAllBySchoolId(UUID schoolId) {
        return registrationDao.findAllBySchoolId(schoolId)
                .map(RegistrationMapper::toDomain);
    }
}
