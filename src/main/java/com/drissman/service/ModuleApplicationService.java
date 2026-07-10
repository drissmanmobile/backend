package com.drissman.service;

import com.drissman.domain.model.Module;
import com.drissman.ports.inbound.ModuleUseCase;
import com.drissman.ports.outbound.ModuleRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class ModuleApplicationService implements ModuleUseCase {

    private final ModuleRepositoryPort moduleRepository;

    @Override
    public Flux<Module> getModules(UUID schoolId) {
        return moduleRepository.findBySchoolIdOrderByOrderIndexAsc(schoolId);
    }

    @Override
    public Mono<Module> createModule(UUID schoolId, String name, String category, String description, Integer orderIndex, Integer requiredHours) {
        Module.ModuleCategory moduleCategory;
        try {
            moduleCategory = category != null ? Module.ModuleCategory.valueOf(category.toUpperCase()) : null;
        } catch (IllegalArgumentException e) {
            moduleCategory = Module.ModuleCategory.CODE;
        }
        if (moduleCategory == null) {
            moduleCategory = Module.ModuleCategory.CODE;
        }

        Module module = Module.builder()
                .schoolId(schoolId)
                .name(name)
                .category(moduleCategory)
                .description(description)
                .orderIndex(orderIndex)
                .requiredHours(requiredHours)
                .createdAt(LocalDateTime.now())
                .build();

        return moduleRepository.save(module);
    }

    @Override
    public Mono<Module> updateModule(UUID moduleId, UUID schoolId, String name, String category, String description, Integer orderIndex, Integer requiredHours) {
        return moduleRepository.findById(moduleId)
                .filter(module -> schoolId.equals(module.getSchoolId()))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Module non trouve")))
                .flatMap(existing -> {
                    existing.setName(name);
                    existing.setDescription(description);
                    existing.setOrderIndex(orderIndex);
                    existing.setRequiredHours(requiredHours);

                    if (category != null) {
                        try {
                            existing.setCategory(Module.ModuleCategory.valueOf(category.toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            // keep existing category
                        }
                    }

                    return moduleRepository.save(existing);
                });
    }

    @Override
    public Mono<Void> deleteModule(UUID moduleId, UUID schoolId) {
        return moduleRepository.findById(moduleId)
                .filter(module -> schoolId.equals(module.getSchoolId()))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Module non trouve")))
                .flatMap(moduleRepository::delete);
    }
}
