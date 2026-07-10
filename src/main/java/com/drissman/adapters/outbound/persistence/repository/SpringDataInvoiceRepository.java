package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.InvoiceEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface SpringDataInvoiceRepository extends ReactiveCrudRepository<InvoiceEntity, UUID> {
    Flux<InvoiceEntity> findByUserId(UUID userId);

    Flux<InvoiceEntity> findByEnrollmentId(UUID enrollmentId);

    Flux<InvoiceEntity> findBySchoolId(UUID schoolId);
}
