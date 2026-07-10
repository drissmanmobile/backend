package com.drissman.service;

import com.drissman.domain.model.Offer;
import com.drissman.ports.inbound.OfferUseCase;
import com.drissman.ports.outbound.OfferRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class OfferApplicationService implements OfferUseCase {

    private final OfferRepositoryPort offerRepositoryPort;

    @Override
    public Mono<Offer> create(UUID schoolId, String name, String description, Integer price, Integer hours, String permitType) {
        Offer offer = Offer.builder()
                .schoolId(schoolId)
                .name(name)
                .description(description)
                .price(price)
                .hours(hours)
                .permitType(permitType)
                .build();

        return offerRepositoryPort.save(offer);
    }

    @Override
    public Flux<Offer> findBySchoolId(UUID schoolId) {
        return offerRepositoryPort.findBySchoolId(schoolId);
    }

    @Override
    public Mono<Offer> findById(UUID id) {
        return offerRepositoryPort.findById(id);
    }

    @Override
    public Mono<Offer> update(UUID schoolId, UUID id, String name, String description, Integer price, Integer hours, String permitType) {
        return offerRepositoryPort.findById(id)
                .filter(offer -> schoolId.equals(offer.getSchoolId()))
                .flatMap(offer -> {
                    if (name != null)
                        offer.setName(name);
                    if (description != null)
                        offer.setDescription(description);
                    if (price != null)
                        offer.setPrice(price);
                    if (hours != null)
                        offer.setHours(hours);
                    if (permitType != null)
                        offer.setPermitType(permitType);
                    return offerRepositoryPort.save(offer);
                });
    }

    @Override
    public Mono<Void> delete(UUID schoolId, UUID id) {
        return offerRepositoryPort.findById(id)
                .filter(offer -> schoolId.equals(offer.getSchoolId()))
                .flatMap(offerRepositoryPort::delete);
    }
}
