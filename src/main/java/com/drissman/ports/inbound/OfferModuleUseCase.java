package com.drissman.ports.inbound;

import com.drissman.domain.model.OfferModuleDetails;
import lombok.Value;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface OfferModuleUseCase {
    Flux<OfferModuleDetails> getModulesForOffer(UUID offerId);
    Flux<OfferModuleDetails> setModulesForOffer(UUID schoolId, UUID offerId, List<ModuleEntry> modules);
    Mono<OfferModuleDetails> addModuleToOffer(UUID offerId, UUID moduleId, Integer orderIndex);
    Mono<Void> removeModuleFromOffer(UUID schoolId, UUID offerId, UUID moduleId);

    @Value
    class ModuleEntry {
        UUID moduleId;
        Integer orderIndex;
    }
}
