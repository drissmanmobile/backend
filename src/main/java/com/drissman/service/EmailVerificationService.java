package com.drissman.service;

import com.drissman.ports.inbound.ResendVerificationUseCase;
import com.drissman.ports.inbound.VerifyEmailUseCase;
import com.drissman.ports.outbound.KernelAuthPort;
import com.drissman.ports.outbound.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService implements VerifyEmailUseCase, ResendVerificationUseCase {

    private final KernelAuthPort kernelAuthPort;
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public Mono<Void> verifyEmail(String email, String verificationToken) {
        log.info("Début de la vérification email via Kernel Core pour : {}", email);
        
        // 1. Confirmer le token avec Kernel Core
        return kernelAuthPort.confirmEmailVerification(verificationToken)
                // 2. Si succès, mettre à jour la base de données locale
                .then(userRepositoryPort.findFirstByEmailIgnoreCase(email.trim().toLowerCase()))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable localement.")))
                .flatMap(user -> {
                    if (user.getIsVerified() != null && user.getIsVerified()) {
                        log.info("L'utilisateur {} était déjà vérifié localement.", email);
                        return Mono.empty();
                    }
                    user.setIsVerified(true);
                    user.setOtpCode(null); // Nettoyage de l'ancien système OTP local
                    user.setOtpExpiry(null);
                    log.info("Mise à jour du statut isVerified à true pour : {}", email);
                    return userRepositoryPort.save(user).then();
                })
                .doOnError(e -> log.error("Erreur lors de la vérification de l'email {} : {}", email, e.getMessage()));
    }

    @Override
    public Mono<Void> resendVerification(String email) {
        log.info("Demande de renvoi du lien/code de vérification pour : {}", email);
        
        // On peut vérifier si l'utilisateur existe localement avant d'appeler Kernel Core
        return userRepositoryPort.findFirstByEmailIgnoreCase(email.trim().toLowerCase())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable.")))
                .flatMap(user -> {
                    if (user.getIsVerified() != null && user.getIsVerified()) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce compte est déjà vérifié."));
                    }
                    // Demander à Kernel Core de renvoyer l'email
                    return kernelAuthPort.resendEmailVerification(email.trim().toLowerCase());
                })
                .doOnError(e -> log.error("Erreur lors du renvoi de vérification pour {} : {}", email, e.getMessage()));
    }
}
