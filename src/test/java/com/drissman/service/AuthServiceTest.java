package com.drissman.service;

import com.drissman.adapters.inbound.rest.dto.AuthResponse;
import com.drissman.adapters.inbound.rest.dto.RegisterRequest;
import com.drissman.domain.model.User;
import com.drissman.ports.outbound.SchoolRepositoryPort;
import com.drissman.ports.outbound.UserRepositoryPort;
import com.drissman.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import org.springframework.web.reactive.function.client.WebClient;
import com.drissman.ports.outbound.KernelAuthPort;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

        private final UserRepositoryPort userRepository = org.mockito.Mockito.mock(UserRepositoryPort.class);

        @Mock
        private SchoolRepositoryPort schoolRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private JwtTokenProvider jwtTokenProvider;

        @Mock
        private WebClient.Builder webClientBuilder;

        @Mock
        private KernelAuthPort kernelAuthPort;

        @InjectMocks
        private AuthService authService;

        @Test
        void registerVisitor_shouldSucceed() {
                RegisterRequest request = new RegisterRequest();
                request.setEmail("visitor@example.com");
                request.setPassword("password123");
                request.setFirstName("John");
                request.setLastName("Visitor");
                request.setRole("visitor"); // Lowercase as sent by frontend

                when(userRepository.existsByEmail(request.getEmail())).thenReturn(Mono.just(false));
                when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

                User savedUser = User.builder()
                                .id(UUID.randomUUID())
                                .email(request.getEmail())
                                .firstName(request.getFirstName())
                                .lastName(request.getLastName())
                                .role(User.Role.VISITOR)
                                .build();

                when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));
                when(jwtTokenProvider.generateToken(any(), any(), any(), any())).thenReturn("mockToken");
                WebClient mockWebClient = org.mockito.Mockito.mock(WebClient.class);
                WebClient.RequestBodyUriSpec mockUriSpec = org.mockito.Mockito.mock(WebClient.RequestBodyUriSpec.class);
                WebClient.RequestBodySpec mockBodySpec = org.mockito.Mockito.mock(WebClient.RequestBodySpec.class);
                WebClient.ResponseSpec mockResponseSpec = org.mockito.Mockito.mock(WebClient.ResponseSpec.class);

                when(webClientBuilder.build()).thenReturn(mockWebClient);
                when(mockWebClient.post()).thenReturn(mockUriSpec);
                when(mockUriSpec.uri(any(String.class))).thenReturn(mockBodySpec);
                org.mockito.Mockito.doReturn(mockBodySpec).when(mockBodySpec).header(anyString(), anyString());
                org.mockito.Mockito.doReturn(mockBodySpec).when(mockBodySpec).bodyValue(any());
                org.mockito.Mockito.doReturn(mockResponseSpec).when(mockBodySpec).retrieve();
                org.mockito.Mockito.doReturn(mockResponseSpec).when(mockResponseSpec).onStatus(any(), any());
                org.mockito.Mockito.doReturn(Mono.just("mockResponse")).when(mockResponseSpec).bodyToMono(String.class);

                when(kernelAuthPort.resendEmailVerification(any())).thenReturn(Mono.empty());

                Mono<AuthResponse> result = authService.register(request);

                StepVerifier.create(result)
                                .expectNextMatches(response -> response.getToken().equals("mockToken") &&
                                                response.getUser().getRole().equals("VISITOR"))
                                .verifyComplete();
        }
}
