package com.drissman.adapters.outbound.persistence;

import com.drissman.adapters.outbound.persistence.entity.InvoiceEntity;
import com.drissman.adapters.outbound.persistence.mapper.InvoicePersistenceMapper;
import com.drissman.adapters.outbound.persistence.repository.SpringDataInvoiceRepository;
import com.drissman.domain.model.Invoice;
import com.drissman.ports.outbound.InvoiceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InvoicePersistenceAdapter implements InvoiceRepositoryPort {

    private final SpringDataInvoiceRepository repository;

    @Override
    public Flux<Invoice> findByUserId(UUID userId) {
        return repository.findByUserId(userId)
                .map(InvoicePersistenceMapper::toDomain);
    }

    @Override
    public Flux<Invoice> findByEnrollmentId(UUID enrollmentId) {
        return repository.findByEnrollmentId(enrollmentId)
                .map(InvoicePersistenceMapper::toDomain);
    }

    @Override
    public Flux<Invoice> findBySchoolId(UUID schoolId) {
        return repository.findBySchoolId(schoolId)
                .map(InvoicePersistenceMapper::toDomain);
    }

    @Override
    public Mono<Invoice> findById(UUID id) {
        return repository.findById(id)
                .map(InvoicePersistenceMapper::toDomain);
    }

    @Override
    public Mono<Invoice> save(Invoice invoice) {
        if (invoice.getId() == null) {
            InvoiceEntity entity = InvoicePersistenceMapper.toEntity(invoice);
            entity.setNewEntity(true);
            return repository.save(entity)
                    .map(InvoicePersistenceMapper::toDomain);
        }

        return repository.existsById(invoice.getId())
                .flatMap(exists -> {
                    InvoiceEntity entity = InvoicePersistenceMapper.toEntity(invoice);
                    entity.setNewEntity(!exists);
                    return repository.save(entity);
                })
                .map(InvoicePersistenceMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }
}
