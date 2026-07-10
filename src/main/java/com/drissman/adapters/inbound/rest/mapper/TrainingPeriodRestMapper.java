package com.drissman.adapters.inbound.rest.mapper;

import com.drissman.adapters.inbound.rest.dto.TrainingPeriodFormationDto;
import com.drissman.adapters.inbound.rest.dto.TrainingPeriodViewDto;
import com.drissman.domain.model.TrainingPeriodDetails;
import com.drissman.domain.model.TrainingPeriodFormation;

import java.util.ArrayList;
import java.util.List;

public class TrainingPeriodRestMapper {

    private TrainingPeriodRestMapper() {
        // Utility class
    }

    public static TrainingPeriodViewDto toViewDto(TrainingPeriodDetails domain) {
        if (domain == null) {
            return null;
        }

        List<TrainingPeriodFormationDto> formationDtos = new ArrayList<>();
        if (domain.getFormations() != null) {
            for (TrainingPeriodFormation formation : domain.getFormations()) {
                formationDtos.add(toFormationDto(formation));
            }
        }

        return TrainingPeriodViewDto.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .enrollmentDeadline(domain.getEnrollmentDeadline())
                .maxStudents(domain.getMaxStudents())
                .status(domain.getStatus())
                .totalEnrolled(domain.getTotalEnrolled())
                .formations(formationDtos)
                .build();
    }

    public static TrainingPeriodFormationDto toFormationDto(TrainingPeriodFormation formation) {
        if (formation == null) {
            return null;
        }
        return TrainingPeriodFormationDto.builder()
                .offerId(formation.getOfferId())
                .offerName(formation.getOfferName())
                .permitType(formation.getPermitType())
                .price(formation.getPrice())
                .build();
    }
}
