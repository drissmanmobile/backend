package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.CreateTrainingPeriodRequest;
import com.drissman.adapters.inbound.rest.dto.TrainingPeriodViewDto;
import com.drissman.adapters.inbound.rest.dto.UpdateTrainingPeriodStatusRequest;
import com.drissman.adapters.inbound.rest.mapper.TrainingPeriodRestMapper;
import com.drissman.domain.model.User;
import com.drissman.ports.inbound.TrainingPeriodUseCase;
import com.drissman.ports.outbound.UserRepositoryPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/schools/admin/training-periods")
@RequiredArgsConstructor
public class AdminTrainingPeriodController {

    private final TrainingPeriodUseCase trainingPeriodUseCase;
    private final UserRepositoryPort userRepository;

    @GetMapping
    public Flux<TrainingPeriodViewDto> list(Principal principal) {
        return getSchoolId(principal)
                .flatMapMany(schoolId -> trainingPeriodUseCase.getBySchool(schoolId)
                        .map(TrainingPeriodRestMapper::toViewDto));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TrainingPeriodViewDto> create(
            Principal principal,
            @Valid @RequestBody CreateTrainingPeriodRequest request) {
        return getSchoolId(principal)
                .flatMap(schoolId -> trainingPeriodUseCase.create(
                        schoolId,
                        request.getName(),
                        request.getDescription(),
                        request.getStartDate(),
                        request.getEndDate(),
                        request.getMaxStudents(),
                        request.getEnrollmentDeadline(),
                        request.getScheduleDescription(),
                        request.getOfferId(),
                        request.getOfferIds()
                ))
                .map(TrainingPeriodRestMapper::toViewDto);
    }

    @PatchMapping("/{id}/status")
    public Mono<TrainingPeriodViewDto> updateStatus(
            Principal principal,
            @PathVariable UUID id,
            @RequestBody UpdateTrainingPeriodStatusRequest request) {
        return getSchoolId(principal)
                .flatMap(schoolId -> trainingPeriodUseCase.updateStatus(schoolId, id, request.getStatus()))
                .map(TrainingPeriodRestMapper::toViewDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(
            Principal principal,
            @PathVariable UUID id) {
        return getSchoolId(principal)
                .flatMap(schoolId -> trainingPeriodUseCase.delete(schoolId, id));
    }

    private Mono<UUID> getSchoolId(Principal principal) {
        if (principal == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentification requise"));
        }
        return userRepository.findById(UUID.fromString(principal.getName()))
                .map(User::getSchoolId)
                .filter(schoolId -> schoolId != null)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Compte non associe a une ecole")));
    }
}
