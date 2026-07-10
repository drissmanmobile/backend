package com.drissman.ports.inbound;

import com.drissman.domain.model.Module;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ModuleUseCase {
    Flux<Module> getModules(UUID schoolId);
    Mono<Module> createModule(UUID schoolId, String name, String category, String description, Integer orderIndex, Integer requiredHours);
    Mono<Module> updateModule(UUID moduleId, UUID schoolId, String name, String category, String description, Integer orderIndex, Integer requiredHours);
    Mono<Void> deleteModule(UUID moduleId, UUID schoolId);
}
