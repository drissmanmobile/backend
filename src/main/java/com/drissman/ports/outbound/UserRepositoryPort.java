package com.drissman.ports.outbound;

import com.drissman.domain.model.User;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepositoryPort {
    Mono<User> findById(UUID id);
    Mono<User> findByEmail(String email);
    Mono<User> findFirstByEmailIgnoreCase(String email);
    Mono<Boolean> existsByEmail(String email);
    Mono<User> save(User user);
    Mono<Void> deleteById(UUID id);
}
