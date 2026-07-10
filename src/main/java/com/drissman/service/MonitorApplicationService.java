package com.drissman.service;

import com.drissman.domain.model.Monitor;
import com.drissman.domain.model.User;
import com.drissman.ports.outbound.UserRepositoryPort;
import com.drissman.ports.inbound.MonitorUseCase;
import com.drissman.ports.outbound.MonitorRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class MonitorApplicationService implements MonitorUseCase {

    private final MonitorRepositoryPort monitorRepositoryPort;
    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<Monitor> createMonitor(UUID schoolId, String firstName, String lastName, String email, String password, String licenseNumber, String phoneNumber) {
        String normalizedEmail = email != null ? email.trim().toLowerCase() : null;
        String normalizedPassword = password != null ? password.trim() : null;

        if (normalizedEmail != null && !normalizedEmail.isBlank() &&
                normalizedPassword != null && !normalizedPassword.isBlank()) {
                
            if (normalizedPassword.length() < 8) {
                return Mono.error(new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Le mot de passe doit contenir au moins 8 caractères"));
            }

            User newUser = User.builder()
                    .schoolId(schoolId)
                    .email(normalizedEmail)
                    .password(passwordEncoder.encode(normalizedPassword))
                    .firstName(firstName != null ? firstName.trim() : null)
                    .lastName(lastName != null ? lastName.trim() : null)
                    .role(User.Role.MONITOR)
                    .createdAt(LocalDateTime.now())
                    .build();

            return userRepository.save(newUser)
                    .flatMap(savedUser -> saveMonitorEntity(schoolId, firstName, lastName, licenseNumber, phoneNumber, savedUser.getId()));
        } else {
            return saveMonitorEntity(schoolId, firstName, lastName, licenseNumber, phoneNumber, null);
        }
    }

    private Mono<Monitor> saveMonitorEntity(UUID schoolId, String firstName, String lastName, String licenseNumber, String phoneNumber, UUID userId) {
        Monitor monitor = Monitor.builder()
                .schoolId(schoolId)
                .userId(userId)
                .firstName(firstName != null ? firstName.trim() : null)
                .lastName(lastName != null ? lastName.trim() : null)
                .licenseNumber(licenseNumber != null ? licenseNumber.trim() : null)
                .phoneNumber(phoneNumber != null ? phoneNumber.trim() : null)
                .status(Monitor.MonitorStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        return monitorRepositoryPort.save(monitor);
    }

    @Override
    public Flux<Monitor> getMonitorsBySchool(UUID schoolId) {
        return monitorRepositoryPort.findBySchoolId(schoolId);
    }

    @Override
    public Mono<Monitor> getMonitorById(UUID monitorId) {
        return monitorRepositoryPort.findById(monitorId);
    }

    @Override
    public Mono<Monitor> getMonitorByUserId(UUID userId) {
        return monitorRepositoryPort.findByUserId(userId);
    }

    @Override
    public Mono<Monitor> updateMonitor(UUID schoolId, UUID monitorId, String firstName, String lastName, String licenseNumber, String phoneNumber, Monitor.MonitorStatus status) {
        return monitorRepositoryPort.findById(monitorId)
                .filter(monitor -> schoolId.equals(monitor.getSchoolId()))
                .switchIfEmpty(Mono.error(new RuntimeException("Moniteur introuvable pour cette auto-ecole")))
                .flatMap(monitor -> {
                    if (firstName != null)
                        monitor.setFirstName(firstName);
                    if (lastName != null)
                        monitor.setLastName(lastName);
                    if (licenseNumber != null)
                        monitor.setLicenseNumber(licenseNumber);
                    if (phoneNumber != null)
                        monitor.setPhoneNumber(phoneNumber);
                    if (status != null)
                        monitor.setStatus(status);

                    return monitorRepositoryPort.save(monitor);
                });
    }

    @Override
    public Mono<Void> deleteMonitor(UUID schoolId, UUID monitorId) {
        return monitorRepositoryPort.findById(monitorId)
                .filter(monitor -> schoolId.equals(monitor.getSchoolId()))
                .switchIfEmpty(Mono.error(new RuntimeException("Moniteur introuvable pour cette auto-ecole")))
                .flatMap(monitor -> {
                    if (monitor.getUserId() != null) {
                        return userRepository.deleteById(monitor.getUserId())
                                .then(monitorRepositoryPort.deleteById(monitor.getId()));
                    }
                    return monitorRepositoryPort.deleteById(monitor.getId());
                });
    }
}
