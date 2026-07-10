package com.drissman.ports.outbound;

import com.drissman.domain.model.Invoice;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface InvoiceRepositoryPort {
    Flux<Invoice> findByUserId(UUID userId);
    Flux<Invoice> findByEnrollmentId(UUID enrollmentId);
    Flux<Invoice> findBySchoolId(UUID schoolId);
    Mono<Invoice> findById(UUID id);
    Mono<Invoice> save(Invoice invoice);
    Mono<Void> deleteById(UUID id);
}
