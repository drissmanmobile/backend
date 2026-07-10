package com.drissman.adapters.outbound.persistence;

import com.drissman.adapters.outbound.persistence.mapper.LessonRegistrationPersistenceMapper;
import com.drissman.adapters.outbound.persistence.repository.SpringDataLessonRegistrationRepository;
import com.drissman.domain.model.LessonRegistration;
import com.drissman.ports.outbound.LessonRegistrationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LessonRegistrationPersistenceAdapter implements LessonRegistrationRepositoryPort {

    private final SpringDataLessonRegistrationRepository repository;

    @Override
    public Flux<LessonRegistration> findByLessonId(UUID lessonId) {
        return repository.findByLessonId(lessonId)
                .map(LessonRegistrationPersistenceMapper::toDomain);
    }

    @Override
    public Flux<LessonRegistration> findByStudentId(UUID studentId) {
        return repository.findByStudentId(studentId)
                .map(LessonRegistrationPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByLessonIdAndStudentId(UUID lessonId, UUID studentId) {
        return repository.existsByLessonIdAndStudentId(lessonId, studentId);
    }

    @Override
    public Mono<Long> countByLessonId(UUID lessonId) {
        return repository.countByLessonId(lessonId);
    }

    @Override
    public Mono<Void> deleteByLessonIdAndStudentId(UUID lessonId, UUID studentId) {
        return repository.deleteByLessonIdAndStudentId(lessonId, studentId);
    }

    @Override
    public Mono<LessonRegistration> findById(UUID id) {
        return repository.findById(id)
                .map(LessonRegistrationPersistenceMapper::toDomain);
    }

    @Override
    public Mono<LessonRegistration> save(LessonRegistration lessonRegistration) {
        return repository.save(LessonRegistrationPersistenceMapper.toEntity(lessonRegistration))
                .map(LessonRegistrationPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }
}
