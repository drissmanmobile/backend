package com.drissman.service;

import com.drissman.adapters.inbound.rest.dto.AuthResponse;
import com.drissman.adapters.inbound.rest.dto.LoginRequest;
import com.drissman.adapters.inbound.rest.dto.RegisterRequest;
import com.drissman.adapters.inbound.rest.dto.UpgradeVisitorRoleRequest;
import com.drissman.domain.model.School;
import com.drissman.domain.model.User;
import com.drissman.ports.outbound.SchoolRepositoryPort;
import com.drissman.ports.outbound.UserRepositoryPort;
import com.drissman.security.JwtTokenProvider;
import com.drissman.ports.outbound.KernelAuthPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import com.drissman.adapters.inbound.rest.dto.GoogleLoginRequest;
import com.drissman.adapters.inbound.rest.dto.GoogleTokenInfo;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepositoryPort userRepository;
    private final SchoolRepositoryPort schoolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    private final KernelAuthPort kernelAuthPort;

    @Value("${google.client.id}")
    private String googleClientId;

    public Mono<AuthResponse> register(RegisterRequest request) {
        return userRepository.existsByEmail(request.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists"));
                    }

                    return webClientBuilder.build()
                            .post()
                            .uri("https://kernel-core.yowyob.com/api/auth/sign-up")
                            .header("X-Client-Id", "drissman")
                            .header("X-Api-Key", "1V0ET97W4T2-6ZYnarvtQt4ffg5YUcte0JDwyY5udYzuhKD8")
                            .header("X-Tenant-Id", "drissman")
                            .bodyValue(new KernelSignUpRequest(request.getEmail(), request.getFirstName(), request.getLastName(), request.getPassword()))
                            .retrieve()
                            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                                    response -> response.bodyToMono(String.class).flatMap(errorBody -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, formatKernelError(errorBody)))))
                            .bodyToMono(String.class)
                            .then(Mono.defer(() -> {
                                User.Role roleTemp;
                                try {
                                    roleTemp = User.Role.valueOf(request.getRole().toUpperCase());
                                } catch (IllegalArgumentException | NullPointerException e) {
                                    roleTemp = User.Role.VISITOR; // Fallback to VISITOR if role is invalid or not yet in enum
                                }
                                final User.Role userRole = roleTemp;

                                if (userRole == User.Role.SCHOOL_ADMIN) {
                                    School school = School.builder()
                                            .name(request.getSchoolName() != null ? request.getSchoolName()
                                                    : "Ma Nouvelle Auto-École")
                                            .address("Adresse à compléter")
                                            .city("Yaoundé") // Default city
                                            .build();

                                    return schoolRepository.save(school)
                                            .flatMap(savedSchool -> {
                                                User user = User.builder()
                                                        .email(request.getEmail())
                                                        .password(passwordEncoder.encode(request.getPassword()))
                                                        .firstName(request.getFirstName())
                                                        .lastName(request.getLastName())
                                                        .phone(request.getPhone())
                                                        .role(userRole)
                                                        .schoolId(savedSchool.getId())
                                                        .isVerified(false)
                                                        .build();
                                                return userRepository.save(user);
                                            })
                                            .flatMap(savedUser -> kernelAuthPort.resendEmailVerification(savedUser.getEmail())
                                                    .onErrorResume(e -> {
                                                        log.error("Erreur lors de l'envoi de l'email de vérification à Kernel Core", e);
                                                        return Mono.empty();
                                                    })
                                                    .thenReturn(savedUser)
                                            )
                                            .map(this::createAuthResponse);
                                } else {
                                    // VISITOR/CANDIDAT/MONITOR: simple user creation without school
                                    User user = User.builder()
                                            .email(request.getEmail())
                                            .password(passwordEncoder.encode(request.getPassword()))
                                            .firstName(request.getFirstName())
                                            .lastName(request.getLastName())
                                            .phone(request.getPhone())
                                            .role(userRole)
                                            .isVerified(false)
                                            .build();

                                    return userRepository.save(user)
                                            .flatMap(savedUser -> kernelAuthPort.resendEmailVerification(savedUser.getEmail())
                                                    .onErrorResume(e -> {
                                                        log.error("Erreur lors de l'envoi de l'email de vérification à Kernel Core", e);
                                                        return Mono.empty();
                                                    })
                                                    .thenReturn(savedUser)
                                            )
                                            .map(this::createAuthResponse);
                                }
                            }));
                });
    }

    public Mono<AuthResponse> upgradeVisitorRole(java.util.UUID userId, UpgradeVisitorRoleRequest request) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("Utilisateur non trouvÃ©")))
                .flatMap(user -> {
                    if (user.getRole() != User.Role.VISITOR) {
                        return Mono.error(new RuntimeException("Seul un compte visiteur peut changer de rÃ´le"));
                    }

                    User.Role targetRole;
                    try {
                        targetRole = User.Role.valueOf(request.getTargetRole().toUpperCase());
                    } catch (IllegalArgumentException | NullPointerException e) {
                        return Mono.error(new RuntimeException("RÃ´le cible invalide"));
                    }

                    if (targetRole != User.Role.STUDENT && targetRole != User.Role.SCHOOL_ADMIN) {
                        return Mono.error(new RuntimeException("Le compte visiteur peut devenir STUDENT ou SCHOOL_ADMIN"));
                    }

                    if (targetRole == User.Role.STUDENT) {
                        user.setRole(User.Role.STUDENT);
                        user.setSchoolId(null);
                        return userRepository.save(user).map(this::createAuthResponse);
                    }

                    School school = School.builder()
                            .name(request.getSchoolName() != null && !request.getSchoolName().isBlank()
                                    ? request.getSchoolName()
                                    : "Ma Nouvelle Auto-Ã‰cole")
                            .address("Adresse à compléter")
                            .city("Yaoundé")
                            .isActive(true)
                            .build();

                    return schoolRepository.save(school)
                            .flatMap(savedSchool -> {
                                user.setRole(User.Role.SCHOOL_ADMIN);
                                user.setSchoolId(savedSchool.getId());
                                return userRepository.save(user);
                            })
                            .map(this::createAuthResponse);
                });
    }

    public Mono<AuthResponse> login(LoginRequest request) {
        String normalizedEmail = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        String rawPassword = request.getPassword() != null ? request.getPassword() : "";

        return webClientBuilder.build()
                .post()
                .uri("https://kernel-core.yowyob.com/api/auth/login")
                .header("X-Client-Id", "drissman")
                .header("X-Api-Key", "1V0ET97W4T2-6ZYnarvtQt4ffg5YUcte0JDwyY5udYzuhKD8")
                .header("X-Tenant-Id", "drissman")
                .bodyValue(new KernelLoginRequest(normalizedEmail, rawPassword))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class).flatMap(errorBody -> Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, formatKernelError(errorBody)))))
                .bodyToMono(String.class)
                .then(userRepository.findFirstByEmailIgnoreCase(normalizedEmail))
                .switchIfEmpty(Mono.defer(() -> {
                    // Auto-creation locale si l'utilisateur Kernel Core se connecte pour la première fois
                    User newUser = User.builder()
                            .email(normalizedEmail)
                            .password(passwordEncoder.encode(java.util.UUID.randomUUID().toString()))
                            .firstName("Utilisateur")
                            .lastName("")
                            .role(User.Role.VISITOR)
                            .isVerified(true)
                            .build();
                    return userRepository.save(newUser);
                }))
                .map(this::createAuthResponse);
    }

    public Mono<AuthResponse> loginWithGoogle(GoogleLoginRequest request) {
        return webClientBuilder.build()
                .get()
                .uri("https://oauth2.googleapis.com/tokeninfo?id_token=" + request.getIdToken())
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token Google invalide")))
                .bodyToMono(GoogleTokenInfo.class)
                .flatMap(tokenInfo -> {
                    if (tokenInfo.getAud() == null || !tokenInfo.getAud().equals(googleClientId)) {
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token Google non destiné à cette application"));
                    }
                    
                    String email = tokenInfo.getEmail() != null ? tokenInfo.getEmail().trim().toLowerCase() : "";
                    if (email.isEmpty()) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email introuvable dans le token Google"));
                    }

                    return userRepository.findFirstByEmailIgnoreCase(email)
                            .switchIfEmpty(Mono.defer(() -> {
                                User newUser = User.builder()
                                        .email(email)
                                        .password(passwordEncoder.encode(java.util.UUID.randomUUID().toString()))
                                        .firstName(tokenInfo.getGivenName() != null ? tokenInfo.getGivenName() : (tokenInfo.getName() != null ? tokenInfo.getName() : "Utilisateur"))
                                        .lastName(tokenInfo.getFamilyName() != null ? tokenInfo.getFamilyName() : "")
                                        .role(User.Role.VISITOR)
                                        .build();
                                return userRepository.save(newUser);
                            }))
                            .map(this::createAuthResponse);
                });
    }



    private AuthResponse createAuthResponse(User user) {
        String token = jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                user.getSchoolId());

        return AuthResponse.builder()
                .user(AuthResponse.UserDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .role(user.getRole().name())
                        .schoolId(user.getSchoolId())
                        .isVerified(user.getIsVerified() != null ? user.getIsVerified() : false)
                        .build())
                .token(token)
                .build();
    }

    private String formatKernelError(String errorBody) {
        if (errorBody == null || errorBody.isBlank()) {
            return "Adresse e-mail ou mot de passe incorrect.";
        }
        try {
            JsonNode root = objectMapper.readTree(errorBody);
            if (root.has("message")) {
                String msg = root.get("message").asText();
                String msgLower = msg.toLowerCase();
                
                if (msgLower.contains("password must contain") || msgLower.contains("password policy")) {
                    return "Le mot de passe doit contenir au moins 10 caractères, des majuscules, des minuscules, un chiffre et un symbole spécial.";
                }
                if (msgLower.contains("already exists") || msgLower.contains("already use") || msgLower.contains("duplicate")) {
                    return "Cette adresse e-mail est déjà utilisée pour un autre compte.";
                }
                if (msgLower.contains("bad credentials") || msgLower.contains("invalid credentials") || msgLower.contains("not found")) {
                    return "Adresse e-mail ou mot de passe incorrect.";
                }
                if (msgLower.contains("access denied") || msgLower.contains("forbidden")) {
                    return "Accès refusé par le serveur d'authentification.";
                }
                return msg; // Renvoie le message d'origine s'il n'est pas reconnu
            }
        } catch (Exception e) {
            // Ignorer l'erreur de parsing
        }
        return "Une erreur inattendue est survenue avec le serveur d'authentification.";
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    private static class KernelLoginRequest {
        private String principal;
        private String password;
    }

    @lombok.Data
    private static class KernelSignUpRequest {
        private String email;
        private String firstName;
        private String lastName;
        private String password;
        private String username;

        public KernelSignUpRequest(String email, String firstName, String lastName, String password) {
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.password = password;
            this.username = email;
        }
    }
}
