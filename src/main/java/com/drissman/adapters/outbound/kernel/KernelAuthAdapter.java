package com.drissman.adapters.outbound.kernel;

import com.drissman.adapters.outbound.kernel.dto.CreateActorRequest;
import com.drissman.adapters.outbound.kernel.dto.KernelApiResponse;
import com.drissman.adapters.outbound.kernel.dto.LoginRequest;
import com.drissman.adapters.outbound.kernel.dto.RegisterRequest;
import com.drissman.config.KernelProperties;
import com.drissman.domain.model.kernel.CreateActorCommand;
import com.drissman.domain.model.kernel.KernelLoginResponse;
import com.drissman.domain.model.kernel.KernelRegisterResponse;
import com.drissman.domain.model.kernel.RegisterUserCommand;
import com.drissman.ports.outbound.KernelAuthPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Slf4j
public class KernelAuthAdapter implements KernelAuthPort {

    private final WebClient webClient;
    public KernelAuthAdapter(WebClient.Builder webClientBuilder, KernelProperties kernelProperties, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl(kernelProperties.getBaseUrl())
                .defaultHeader("X-Client-Id", kernelProperties.getClientId())
                .defaultHeader("X-Api-Key", kernelProperties.getApiKey())
                .defaultHeader("X-Tenant-Id", kernelProperties.getTenantId())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public Mono<KernelLoginResponse> login(String principal, String password) {
        LoginRequest request = new LoginRequest(principal, password);

        return webClient.post()
                .uri("/api/auth/login")
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides")))
                .onStatus(status -> status.is5xxServerError(), response -> Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur du serveur d'authentification (Kernel)")))
                .bodyToMono(new ParameterizedTypeReference<KernelApiResponse<Map<String, Object>>>() {})
                .map(response -> {
                    if (!response.isSuccess()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, response.getMessage());
                    }
                    Map<String, Object> data = response.getData();
                    return KernelLoginResponse.builder()
                            .accessToken((String) data.get("accessToken"))
                            .refreshToken((String) data.get("refreshToken"))
                            .expiresIn(data.get("expiresIn") != null ? Long.valueOf(data.get("expiresIn").toString()) : null)
                            .build();
                });
    }

    @Override
    public Mono<String> createActor(CreateActorCommand command, String organizationId) {
        CreateActorRequest request = CreateActorRequest.builder()
                .organizationId(organizationId)
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .email(command.getEmail())
                .phoneNumber(command.getPhoneNumber())
                .type(command.getType() != null ? command.getType() : "PERSON")
                .gender(command.getGender())
                .build();

        return webClient.post()
                .uri("/api/actors")
                .header("X-Organization-Id", organizationId) // Explicitly set for tenant/org separation if needed
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.isError(), response -> response.bodyToMono(String.class).flatMap(errorBody -> {
                    log.error("Erreur lors de la création de l'Actor: {}", errorBody);
                    return Mono.error(new ResponseStatusException(response.statusCode(), "Impossible de créer l'acteur dans Kernel Core"));
                }))
                .bodyToMono(new ParameterizedTypeReference<KernelApiResponse<Map<String, Object>>>() {})
                .map(response -> {
                    if (!response.isSuccess()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, response.getMessage());
                    }
                    // Assuming the response data contains the actor ID in an 'id' field
                    return (String) response.getData().get("id");
                });
    }

    @Override
    public Mono<KernelRegisterResponse> registerUser(RegisterUserCommand command, String actorId) {
        RegisterRequest request = RegisterRequest.builder()
                .actorId(actorId)
                .username(command.getUsername())
                .email(command.getEmail())
                .phoneNumber(command.getPhoneNumber())
                .password(command.getPassword())
                .authProvider(command.getAuthProvider() != null ? command.getAuthProvider() : "LOCAL")
                .externalSubject(command.getExternalSubject())
                .build();

        return webClient.post()
                .uri("/api/auth/register")
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> response.bodyToMono(String.class).flatMap(errorBody -> {
                    log.error("Erreur 4xx Kernel Core (Register): {}", errorBody);
                    return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Impossible de créer le compte. L'email est peut-être déjà utilisé."));
                }))
                .onStatus(status -> status.is5xxServerError(), response -> Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur du serveur d'authentification (Kernel)")))
                .bodyToMono(new ParameterizedTypeReference<KernelApiResponse<Map<String, Object>>>() {})
                .map(response -> {
                    if (!response.isSuccess()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, response.getMessage());
                    }
                    Map<String, Object> data = response.getData();
                    return KernelRegisterResponse.builder()
                            .userId((String) data.get("id"))
                            .username((String) data.get("username"))
                            .email((String) data.get("email"))
                            .build();
                });
    }

    @Override
    public Mono<Void> requestEmailVerification(String userAccessToken) {
        return webClient.post()
                .uri("/api/auth/email-verification/request")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Impossible de demander la vérification d'email.")))
                .onStatus(status -> status.is5xxServerError(), response -> Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur Kernel Core lors de la vérification d'email.")))
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<Void> resendEmailVerification(String email) {
        return webClient.post()
                .uri("/api/auth/email-verification/resend")
                .bodyValue(Map.of("email", email)) // Assume it takes {"email": "..."}
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Impossible de renvoyer le code. Vérifiez que l'email est correct et non déjà vérifié.")))
                .onStatus(status -> status.is5xxServerError(), response -> Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur Kernel Core lors du renvoi du code.")))
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<Void> confirmEmailVerification(String verificationToken) {
        return webClient.post()
                .uri("/api/auth/email-verification/confirm")
                .bodyValue(Map.of("verificationToken", verificationToken))
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le code de vérification est invalide ou expiré.")))
                .onStatus(status -> status.is5xxServerError(), response -> Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur Kernel Core lors de la confirmation d'email.")))
                .bodyToMono(Void.class);
    }
}
