package com.drissman.ports.inbound;

import com.drissman.domain.model.TrainingPeriodDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TrainingPeriodUseCase {
    Flux<TrainingPeriodDetails> getBySchool(UUID schoolId);
    Flux<TrainingPeriodDetails> getPublishedBySchool(UUID schoolId);
    Mono<TrainingPeriodDetails> create(UUID schoolId, String name, String description, LocalDate startDate, LocalDate endDate,
                                       Integer maxStudents, LocalDate enrollmentDeadline, String scheduleDescription,
                                       UUID offerId, List<UUID> offerIds);
    Mono<TrainingPeriodDetails> updateStatus(UUID schoolId, UUID periodId, String statusRaw);
    Mono<Void> delete(UUID schoolId, UUID periodId);
}
