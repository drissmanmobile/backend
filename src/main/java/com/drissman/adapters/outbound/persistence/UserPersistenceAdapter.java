package com.drissman.adapters.outbound.persistence;

import com.drissman.adapters.outbound.persistence.mapper.UserPersistenceMapper;
import com.drissman.adapters.outbound.persistence.repository.SpringDataUserRepository;
import com.drissman.domain.model.User;
import com.drissman.ports.outbound.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository springDataUserRepository;

    @Override
    public Mono<User> findById(UUID id) {
        return springDataUserRepository.findById(id)
                .map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return springDataUserRepository.findByEmail(email)
                .map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Mono<User> findFirstByEmailIgnoreCase(String email) {
        return springDataUserRepository.findFirstByEmailIgnoreCase(email)
                .map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return springDataUserRepository.existsByEmail(email);
    }

    @Override
    public Mono<User> save(User user) {
        return springDataUserRepository.save(UserPersistenceMapper.toEntity(user))
                .map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return springDataUserRepository.deleteById(id);
    }
}
