package com.drissman.service;

import com.drissman.domain.model.OfferModule;
import com.drissman.domain.model.OfferModuleDetails;
import com.drissman.ports.inbound.OfferModuleUseCase;
import com.drissman.ports.outbound.ModuleRepositoryPort;
import com.drissman.ports.outbound.OfferModuleRepositoryPort;
import com.drissman.ports.outbound.OfferRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class OfferModuleApplicationService implements OfferModuleUseCase {

    private final OfferModuleRepositoryPort offerModuleRepository;
    private final ModuleRepositoryPort moduleRepository;
    private final OfferRepositoryPort offerRepository;

    @Override
    public Flux<OfferModuleDetails> getModulesForOffer(UUID offerId) {
        return offerModuleRepository.findByOfferIdOrderByOrderIndexAsc(offerId)
                .flatMap(this::enrich);
    }

    @Override
    public Flux<OfferModuleDetails> setModulesForOffer(UUID schoolId, UUID offerId, List<ModuleEntry> modules) {
        return offerRepository.findById(offerId)
                .filter(offer -> schoolId.equals(offer.getSchoolId()))
                .switchIfEmpty(Mono.error(new RuntimeException("Offre introuvable pour cette auto-ecole")))
                .then(offerModuleRepository.deleteByOfferId(offerId))
                .thenMany(Flux.fromIterable(modules)
                        .index()
                        .flatMap(indexed -> {
                            ModuleEntry entry = indexed.getT2();
                            int order = entry.getOrderIndex() != null
                                    ? entry.getOrderIndex()
                                    : indexed.getT1().intValue();
                            OfferModule om = OfferModule.builder()
                                    .offerId(offerId)
                                    .moduleId(entry.getModuleId())
                                    .orderIndex(order)
                                    .createdAt(LocalDateTime.now())
                                    .build();
                            return offerModuleRepository.save(om);
                        }))
                .thenMany(getModulesForOffer(offerId));
    }

    @Override
    public Mono<OfferModuleDetails> addModuleToOffer(UUID offerId, UUID moduleId, Integer orderIndex) {
        OfferModule om = OfferModule.builder()
                .offerId(offerId)
                .moduleId(moduleId)
                .orderIndex(orderIndex != null ? orderIndex : 0)
                .createdAt(LocalDateTime.now())
                .build();
        return offerModuleRepository.save(om)
                .flatMap(this::enrich);
    }

    @Override
    public Mono<Void> removeModuleFromOffer(UUID schoolId, UUID offerId, UUID moduleId) {
        return offerRepository.findById(offerId)
                .filter(offer -> schoolId.equals(offer.getSchoolId()))
                .switchIfEmpty(Mono.error(new RuntimeException("Offre introuvable pour cette auto-ecole")))
                .then(offerModuleRepository.findByOfferIdAndModuleId(offerId, moduleId))
                .flatMap(offerModuleRepository::delete);
    }

    private Mono<OfferModuleDetails> enrich(OfferModule om) {
        return moduleRepository.findById(om.getModuleId())
                .map(module -> OfferModuleDetails.builder()
                        .id(om.getId())
                        .offerId(om.getOfferId())
                        .moduleId(om.getModuleId())
                        .orderIndex(om.getOrderIndex())
                        .moduleName(module.getName())
                        .moduleCategory(module.getCategory() != null ? module.getCategory().name() : null)
                        .moduleDescription(module.getDescription())
                        .moduleRequiredHours(module.getRequiredHours())
                        .build());
    }
}
