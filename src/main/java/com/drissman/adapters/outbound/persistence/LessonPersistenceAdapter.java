package com.drissman.adapters.outbound.persistence;

import com.drissman.adapters.outbound.persistence.mapper.LessonPersistenceMapper;
import com.drissman.adapters.outbound.persistence.repository.SpringDataLessonRepository;
import com.drissman.domain.model.Lesson;
import com.drissman.ports.outbound.LessonRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LessonPersistenceAdapter implements LessonRepositoryPort {

    private final SpringDataLessonRepository repository;

    @Override
    public Flux<Lesson> findBySchoolId(UUID schoolId) {
        return repository.findBySchoolId(schoolId)
                .map(LessonPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Lesson> findBySchoolIdAndDateBetween(UUID schoolId, LocalDate startDate, LocalDate endDate) {
        return repository.findBySchoolIdAndDateBetween(schoolId, startDate, endDate)
                .map(LessonPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Lesson> findByMonitorId(UUID monitorId) {
        return repository.findByMonitorId(monitorId)
                .map(LessonPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Lesson> findByTrainingPeriodId(UUID trainingPeriodId) {
        return repository.findByTrainingPeriodId(trainingPeriodId)
                .map(LessonPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Lesson> findById(UUID id) {
        return repository.findById(id)
                .map(LessonPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Lesson> save(Lesson lesson) {
        return repository.save(LessonPersistenceMapper.toEntity(lesson))
                .map(LessonPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }
}
