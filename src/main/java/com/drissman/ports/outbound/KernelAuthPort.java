package com.drissman.ports.outbound;

import com.drissman.domain.model.kernel.CreateActorCommand;
import com.drissman.domain.model.kernel.KernelLoginResponse;
import com.drissman.domain.model.kernel.KernelRegisterResponse;
import com.drissman.domain.model.kernel.RegisterUserCommand;
import reactor.core.publisher.Mono;

public interface KernelAuthPort {
    
    /**
     * Authenticates a user against Kernel Core.
     * @param principal The username or email.
     * @param password The password.
     * @return A Mono emitting the login response (tokens).
     */
    Mono<KernelLoginResponse> login(String principal, String password);
    
    /**
     * Creates an Actor in Kernel Core. This is a prerequisite for creating a user account.
     * @param command The actor details.
     * @param organizationId The organization (school) ID to bind this actor to (optional).
     * @return A Mono emitting the created Actor ID.
     */
    Mono<String> createActor(CreateActorCommand command, String organizationId);
    
    /**
     * Registers a new user account in Kernel Core linked to an existing Actor.
     * @param command The registration details.
     * @param actorId The ID of the previously created Actor.
     * @return A Mono emitting the registration response.
     */
    Mono<KernelRegisterResponse> registerUser(RegisterUserCommand command, String actorId);
    
    /**
     * Requests an email verification (typically requires an authenticated user token).
     * @param userAccessToken The JWT token of the user.
     */
    Mono<Void> requestEmailVerification(String userAccessToken);

    /**
     * Resends the email verification code/link to the specified email.
     * @param email The email to resend the code to.
     */
    Mono<Void> resendEmailVerification(String email);

    /**
     * Confirms the email verification using the token/code received by the user.
     * @param verificationToken The OTP or verification token.
     */
    Mono<Void> confirmEmailVerification(String verificationToken);
}
