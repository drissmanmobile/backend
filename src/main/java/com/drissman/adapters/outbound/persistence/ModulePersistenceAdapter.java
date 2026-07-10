package com.drissman.adapters.outbound.persistence;

import com.drissman.adapters.outbound.persistence.mapper.ModulePersistenceMapper;
import com.drissman.adapters.outbound.persistence.repository.SpringDataModuleRepository;
import com.drissman.domain.model.Module;
import com.drissman.ports.outbound.ModuleRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ModulePersistenceAdapter implements ModuleRepositoryPort {

    private final SpringDataModuleRepository repository;

    @Override
    public Flux<Module> findBySchoolIdOrderByOrderIndexAsc(UUID schoolId) {
        return repository.findBySchoolIdOrderByOrderIndexAsc(schoolId)
                .map(ModulePersistenceMapper::toDomain);
    }

    @Override
    public Flux<Module> findBySchoolIdAndCategory(UUID schoolId, Module.ModuleCategory category) {
        String categoryStr = category != null ? category.name() : null;
        return repository.findBySchoolIdAndCategory(schoolId, categoryStr)
                .map(ModulePersistenceMapper::toDomain);
    }

    @Override
    public Mono<Module> findById(UUID id) {
        return repository.findById(id)
                .map(ModulePersistenceMapper::toDomain);
    }

    @Override
    public Mono<Module> save(Module module) {
        return repository.save(ModulePersistenceMapper.toEntity(module))
                .map(ModulePersistenceMapper::toDomain);
    }

    @Override
    public Mono<Void> delete(Module module) {
        return repository.delete(ModulePersistenceMapper.toEntity(module));
    }
}
