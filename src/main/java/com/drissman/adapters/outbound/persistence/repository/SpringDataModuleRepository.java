package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.ModuleEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface SpringDataModuleRepository extends R2dbcRepository<ModuleEntity, UUID> {
    Flux<ModuleEntity> findBySchoolIdOrderByOrderIndexAsc(UUID schoolId);
    Flux<ModuleEntity> findBySchoolIdAndCategory(UUID schoolId, String category);
}
