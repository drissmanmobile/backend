package com.drissman.service;

import com.drissman.domain.model.kernel.CreateActorCommand;
import com.drissman.domain.model.kernel.RegisterUserCommand;
import com.drissman.ports.inbound.RegisterStudentUseCase;
import com.drissman.ports.outbound.KernelAuthPort;
import com.drissman.ports.outbound.UserRepositoryPort;
import com.drissman.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterStudentService implements RegisterStudentUseCase {

    private final KernelAuthPort kernelAuthPort;
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public Mono<String> registerStudent(RegisterUserCommand command, String organizationId) {
        log.info("Début de l'inscription de l'étudiant: {}", command.getEmail());

        // 1. Préparer la création de l'Actor
        // On suppose que le nom et prénom peuvent être extraits de command ou demandés
        // Ici on scinde le username grossièrement, mais idéalement le firstName/lastName serait dans RegisterUserCommand
        String[] parts = command.getUsername().split(" ", 2);
        String firstName = parts.length > 0 ? parts[0] : command.getUsername();
        String lastName = parts.length > 1 ? parts[1] : "";

        CreateActorCommand createActorCommand = CreateActorCommand.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(command.getEmail())
                .phoneNumber(command.getPhoneNumber())
                .type("PERSON") // Valeur par défaut
                .build();

        // 2. Orchestration: Actor -> Register User -> Logic locale
        return kernelAuthPort.createActor(createActorCommand, organizationId)
                .flatMap(actorId -> {
                    log.info("Actor créé avec succès dans Kernel Core: {}", actorId);
                    return kernelAuthPort.registerUser(command, actorId);
                })
                .flatMap(kernelRegisterResponse -> {
                    log.info("Utilisateur enregistré dans Kernel Core: {}", kernelRegisterResponse.getUserId());
                    
                    User user = User.builder()
                            .email(command.getEmail())
                            .firstName(firstName)
                            .lastName(lastName)
                            .phone(command.getPhoneNumber())
                            .role(User.Role.STUDENT)
                            .isVerified(false)
                            .build();
                    
                    return userRepositoryPort.save(user)
                            .thenReturn(kernelRegisterResponse.getUserId());
                })
                .doOnError(e -> log.error("Échec du processus d'inscription pour {}: {}", command.getEmail(), e.getMessage()));
    }
}
