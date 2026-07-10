package com.drissman.ports.outbound;

import com.drissman.domain.model.Module;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ModuleRepositoryPort {
    Flux<Module> findBySchoolIdOrderByOrderIndexAsc(UUID schoolId);
    Flux<Module> findBySchoolIdAndCategory(UUID schoolId, Module.ModuleCategory category);
    Mono<Module> findById(UUID id);
    Mono<Module> save(Module module);
    Mono<Void> delete(Module module);
}
