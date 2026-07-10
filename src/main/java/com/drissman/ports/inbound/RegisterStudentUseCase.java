package com.drissman.ports.inbound;

import com.drissman.domain.model.kernel.RegisterUserCommand;
import reactor.core.publisher.Mono;

public interface RegisterStudentUseCase {
    
    /**
     * Orchestre l'inscription d'un étudiant.
     * 1. Crée un Actor dans Kernel
     * 2. Crée l'utilisateur dans Kernel
     * 3. (Optionnel) Crée le profil local
     * 
     * @param command Les données d'inscription de l'étudiant.
     * @param organizationId L'ID de l'auto-école (optionnel).
     * @return Mono contenant l'ID du nouvel utilisateur Kernel.
     */
    Mono<String> registerStudent(RegisterUserCommand command, String organizationId);
}
