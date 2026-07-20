package com.drissman.service;

import com.drissman.domain.model.Module;
import com.drissman.ports.outbound.ModuleRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModuleApplicationServiceTest {

    @Mock
    private ModuleRepositoryPort moduleRepository;

    @InjectMocks
    private ModuleApplicationService moduleApplicationService;

    private UUID schoolId;
    private UUID moduleId;
    private Module module;

    @BeforeEach
    void setUp() {
        schoolId = UUID.randomUUID();
        moduleId = UUID.randomUUID();
        module = Module.builder()
                .id(moduleId)
                .schoolId(schoolId)
                .name("Code de la route")
                .category(Module.ModuleCategory.CODE)
                .description("Apprentissage théorique")
                .orderIndex(1)
                .requiredHours(20)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getModules_ShouldReturnModulesForSchool() {
        // Arrange
        when(moduleRepository.findBySchoolIdOrderByOrderIndexAsc(schoolId))
                .thenReturn(Flux.just(module));

        // Act
        Flux<Module> result = moduleApplicationService.getModules(schoolId);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(m -> m.getName().equals("Code de la route") && m.getSchoolId().equals(schoolId))
                .verifyComplete();

        verify(moduleRepository, times(1)).findBySchoolIdOrderByOrderIndexAsc(schoolId);
    }

    @Test
    void createModule_ShouldSaveAndReturnModule() {
        // Arrange
        when(moduleRepository.save(any(Module.class))).thenReturn(Mono.just(module));

        // Act
        Mono<Module> result = moduleApplicationService.createModule(
                schoolId, "Code de la route", "CODE", "Apprentissage théorique", 1, 20
        );

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(m -> m.getName().equals("Code de la route") && m.getCategory() == Module.ModuleCategory.CODE)
                .verifyComplete();

        verify(moduleRepository, times(1)).save(any(Module.class));
    }

    @Test
    void createModule_WithInvalidCategory_ShouldDefaultToCode() {
        // Arrange
        Module expectedModule = Module.builder()
                .id(UUID.randomUUID())
                .schoolId(schoolId)
                .name("Test invalid category")
                .category(Module.ModuleCategory.CODE) // Default category
                .build();

        when(moduleRepository.save(any(Module.class))).thenReturn(Mono.just(expectedModule));

        // Act
        Mono<Module> result = moduleApplicationService.createModule(
                schoolId, "Test invalid category", "INVALID_CAT", "Test desc", 1, 10
        );

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(m -> m.getCategory() == Module.ModuleCategory.CODE)
                .verifyComplete();
    }

    @Test
    void updateModule_WhenModuleExistsAndBelongsToSchool_ShouldUpdate() {
        // Arrange
        when(moduleRepository.findById(moduleId)).thenReturn(Mono.just(module));
        when(moduleRepository.save(any(Module.class))).thenReturn(Mono.just(module));

        // Act
        Mono<Module> result = moduleApplicationService.updateModule(
                moduleId, schoolId, "Nouveau nom", "DRIVING", "Nouvelle desc", 2, 25
        );

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(m -> m.getId().equals(moduleId))
                .verifyComplete();

        verify(moduleRepository, times(1)).save(any(Module.class));
    }

    @Test
    void updateModule_WhenModuleDoesNotBelongToSchool_ShouldThrowNotFound() {
        // Arrange
        UUID otherSchoolId = UUID.randomUUID();
        when(moduleRepository.findById(moduleId)).thenReturn(Mono.just(module));

        // Act
        Mono<Module> result = moduleApplicationService.updateModule(
                moduleId, otherSchoolId, "Nouveau nom", "DRIVING", "Nouvelle desc", 2, 25
        );

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof ResponseStatusException &&
                        ((ResponseStatusException) throwable).getStatusCode() == HttpStatus.NOT_FOUND)
                .verify();

        verify(moduleRepository, never()).save(any(Module.class));
    }

    @Test
    void deleteModule_WhenValid_ShouldDelete() {
        // Arrange
        when(moduleRepository.findById(moduleId)).thenReturn(Mono.just(module));
        when(moduleRepository.delete(module)).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = moduleApplicationService.deleteModule(moduleId, schoolId);

        // Assert
        StepVerifier.create(result).verifyComplete();

        verify(moduleRepository, times(1)).delete(module);
    }
}
