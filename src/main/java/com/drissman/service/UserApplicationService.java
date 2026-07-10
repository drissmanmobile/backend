package com.drissman.service;

import com.drissman.domain.model.User;
import com.drissman.ports.inbound.UserUseCase;
import com.drissman.ports.outbound.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class UserApplicationService implements UserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<User> findById(UUID id) {
        return userRepositoryPort.findById(id);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return userRepositoryPort.findByEmail(email);
    }

    @Override
    public Mono<User> updateProfile(UUID userId, String firstName, String lastName, String email) {
        return userRepositoryPort.findById(userId)
                .flatMap(user -> {
                    if (firstName != null) {
                        user.setFirstName(firstName);
                    }
                    if (lastName != null) {
                        user.setLastName(lastName);
                    }
                    if (email != null) {
                        user.setEmail(email);
                    }
                    return userRepositoryPort.save(user);
                });
    }

    @Override
    public Mono<Void> changePassword(UUID userId, String currentPassword, String newPassword) {
        return userRepositoryPort.findById(userId)
                .flatMap(user -> {
                    if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                        return Mono.error(new IllegalArgumentException("Mot de passe actuel incorrect"));
                    }
                    user.setPassword(passwordEncoder.encode(newPassword));
                    return userRepositoryPort.save(user);
                })
                .then();
    }
}
