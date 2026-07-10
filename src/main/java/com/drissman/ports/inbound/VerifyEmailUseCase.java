package com.drissman.ports.inbound;

import reactor.core.publisher.Mono;

public interface VerifyEmailUseCase {
    
    /**
     * Confirms the user's email verification using the token provided by Kernel Core.
     * Updates the local User database to set isVerified = true.
     * 
     * @param email The email of the user (to update the local DB).
     * @param verificationToken The token or code sent to the user's email.
     * @return A Mono emitting when the process is complete.
     */
    Mono<Void> verifyEmail(String email, String verificationToken);
}
