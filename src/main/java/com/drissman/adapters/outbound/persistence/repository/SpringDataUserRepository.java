package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.UserEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SpringDataUserRepository extends ReactiveCrudRepository<UserEntity, UUID> {
    @org.springframework.data.r2dbc.repository.Query("SELECT * FROM users WHERE email = :email")
    Mono<UserEntity> findByEmail(String email);

    @org.springframework.data.r2dbc.repository.Query("SELECT * FROM users WHERE LOWER(TRIM(email)) = LOWER(TRIM(:email)) LIMIT 1")
    Mono<UserEntity> findFirstByEmailIgnoreCase(String email);

    @org.springframework.data.r2dbc.repository.Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
    Mono<Boolean> existsByEmail(String email);
}
