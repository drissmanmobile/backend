package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.TrainingPeriodViewDto;
import com.drissman.adapters.inbound.rest.mapper.TrainingPeriodRestMapper;
import com.drissman.ports.inbound.TrainingPeriodUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RestController
@RequestMapping("/api/training-periods/published")
@RequiredArgsConstructor
public class PublicTrainingPeriodController {

    private final TrainingPeriodUseCase trainingPeriodUseCase;

    @GetMapping("/school/{schoolId}")
    public Flux<TrainingPeriodViewDto> listBySchool(@PathVariable UUID schoolId) {
        return trainingPeriodUseCase.getPublishedBySchool(schoolId)
                .map(TrainingPeriodRestMapper::toViewDto);
    }
}
