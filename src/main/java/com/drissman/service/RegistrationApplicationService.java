package com.drissman.service;

import com.drissman.domain.model.Registration;
import com.drissman.domain.model.User;
import com.drissman.ports.inbound.RegistrationUseCase;
import com.drissman.ports.outbound.RegistrationRepositoryPort;
import com.drissman.ports.outbound.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationApplicationService implements RegistrationUseCase {

    private final RegistrationRepositoryPort registrationRepository;
    private final UserRepositoryPort userRepository;

    @Override
    public Mono<Registration> createRegistration(UUID userId, UUID schoolId, UUID formationId, String category, String remarks, String phone, String cniNumber, String birthDate, String address) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé")))
                .flatMap(user -> {
                    if (phone != null) user.setPhone(phone);
                    if (cniNumber != null) user.setCniNumber(cniNumber);
                    if (address != null) user.setAddress(address);
                    if (birthDate != null && !birthDate.isBlank()) {
                        try {
                            user.setBirthDate(java.time.LocalDate.parse(birthDate));
                        } catch (Exception e) {
                            // ignore or handle date parsing error
                        }
                    }
                    return userRepository.save(user);
                })
                .then(Mono.defer(() -> {
                    Registration registration = Registration.builder()
                            .id(UUID.randomUUID())
                            .userId(userId)
                            .schoolId(schoolId)
                            .formationId(formationId)
                            .category(category)
                            .status(Registration.RegistrationStatus.PENDING)
                            .paymentStatus(Registration.PaymentStatus.NOT_REQUIRED)
                            .documentsStatus(Registration.DocumentsStatus.NOT_REQUIRED)
                            .createdAt(LocalDateTime.now())
                            .remarks(remarks)
                            .build();
                    return registrationRepository.save(registration);
                }));
    }

    @Override
    public Mono<Registration> getRegistration(UUID id) {
        return registrationRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscription non trouvée")));
    }

    @Override
    public Flux<Registration> getMyRegistrations(UUID userId) {
        return registrationRepository.findAllByUserId(userId);
    }

    @Override
    public Flux<Registration> getAdminRegistrations(UUID schoolId) {
        return registrationRepository.findAllBySchoolId(schoolId);
    }

    @Override
    public Mono<Registration> approveRegistration(UUID id, UUID adminId, String remarks, UUID monitorId) {
        if (monitorId == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'affectation à un moniteur de suivi est obligatoire"));
        }
        return getRegistration(id)
                .flatMap(registration -> {
                    if (registration.getStatus() == Registration.RegistrationStatus.APPROVED) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette inscription est déjà approuvée"));
                    }
                    
                    registration.setStatus(Registration.RegistrationStatus.APPROVED);
                    registration.setValidatedAt(LocalDateTime.now());
                    registration.setValidatedBy(adminId);
                    if (remarks != null && !remarks.isBlank()) {
                        registration.setRemarks(remarks);
                    }

                    // Update User
                    return userRepository.findById(registration.getUserId())
                            .flatMap(user -> {
                                user.setRole(User.Role.STUDENT);
                                user.setSchoolId(registration.getSchoolId());
                                user.setMonitorId(monitorId);
                                // The class is implicitly determined by the formation they chose
                                return userRepository.save(user);
                            })
                            .then(registrationRepository.save(registration));
                });
    }

    @Override
    public Mono<Registration> rejectRegistration(UUID id, UUID adminId, String remarks) {
        return getRegistration(id)
                .flatMap(registration -> {
                    if (registration.getStatus() == Registration.RegistrationStatus.REJECTED) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette inscription est déjà rejetée"));
                    }
                    
                    registration.setStatus(Registration.RegistrationStatus.REJECTED);
                    registration.setValidatedAt(LocalDateTime.now());
                    registration.setValidatedBy(adminId);
                    if (remarks != null && !remarks.isBlank()) {
                        registration.setRemarks(remarks);
                    }

                    return registrationRepository.save(registration);
                });
    }
}
