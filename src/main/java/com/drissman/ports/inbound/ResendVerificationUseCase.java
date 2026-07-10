package com.drissman.ports.inbound;

import reactor.core.publisher.Mono;

public interface ResendVerificationUseCase {
    
    /**
     * Resends the email verification code/link via Kernel Core.
     * 
     * @param email The email to which the code should be sent.
     * @return A Mono emitting when the process is complete.
     */
    Mono<Void> resendVerification(String email);
}
