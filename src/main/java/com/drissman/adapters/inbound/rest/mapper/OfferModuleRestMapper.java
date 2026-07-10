package com.drissman.adapters.inbound.rest.mapper;

import com.drissman.adapters.inbound.rest.dto.OfferModuleDto;
import com.drissman.adapters.inbound.rest.dto.SetOfferModulesRequest;
import com.drissman.domain.model.OfferModuleDetails;
import com.drissman.ports.inbound.OfferModuleUseCase;

public class OfferModuleRestMapper {

    private OfferModuleRestMapper() {
        // Utility class
    }

    public static OfferModuleDto toDto(OfferModuleDetails domain) {
        if (domain == null) {
            return null;
        }
        return OfferModuleDto.builder()
                .id(domain.getId())
                .offerId(domain.getOfferId())
                .moduleId(domain.getModuleId())
                .orderIndex(domain.getOrderIndex())
                .moduleName(domain.getModuleName())
                .moduleCategory(domain.getModuleCategory())
                .moduleDescription(domain.getModuleDescription())
                .moduleRequiredHours(domain.getModuleRequiredHours())
                .build();
    }

    public static OfferModuleUseCase.ModuleEntry toUseCaseEntry(SetOfferModulesRequest.ModuleEntry restEntry) {
        if (restEntry == null) {
            return null;
        }
        return new OfferModuleUseCase.ModuleEntry(restEntry.getModuleId(), restEntry.getOrderIndex());
    }
}
