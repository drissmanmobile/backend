package com.drissman.ports.inbound;

import com.drissman.domain.model.User;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserUseCase {
    Mono<User> findById(UUID id);
    Mono<User> findByEmail(String email);
    Mono<User> updateProfile(UUID userId, String firstName, String lastName, String email);
    Mono<Void> changePassword(UUID userId, String currentPassword, String newPassword);
}
