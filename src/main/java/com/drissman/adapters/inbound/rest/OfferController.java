package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.SchoolDto;
import com.drissman.adapters.inbound.rest.mapper.OfferRestMapper;
import com.drissman.adapters.inbound.rest.dto.OfferModuleDto;
import com.drissman.ports.inbound.OfferUseCase;
import com.drissman.ports.inbound.OfferModuleUseCase;
import com.drissman.adapters.inbound.rest.mapper.OfferModuleRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferUseCase offerUseCase;
    private final OfferModuleUseCase offerModuleUseCase;

    /**
     * Get all offers for a school
     */
    @GetMapping("/school/{schoolId}")
    public Flux<SchoolDto.OfferDto> getBySchool(@PathVariable UUID schoolId) {
        return offerUseCase.findBySchoolId(schoolId)
                .map(OfferRestMapper::toDto);
    }

    /**
     * Get offer by ID
     */
    @GetMapping("/{id}")
    public Mono<SchoolDto.OfferDto> getById(@PathVariable UUID id) {
        return offerUseCase.findById(id)
                .map(OfferRestMapper::toDto);
    }

    // ─── Module association endpoints ───────────────────────────────────

    /**
     * Get all modules for an offer, ordered by orderIndex.
     */
    @GetMapping("/{offerId}/modules")
    public Flux<OfferModuleDto> getModules(@PathVariable UUID offerId) {
        return offerModuleUseCase.getModulesForOffer(offerId)
                .map(OfferModuleRestMapper::toDto);
    }
}
