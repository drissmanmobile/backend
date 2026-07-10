package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.SchoolEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface SpringDataSchoolRepository extends ReactiveCrudRepository<SchoolEntity, UUID> {
    Flux<SchoolEntity> findByCity(String city);

    @Query("SELECT * FROM schools WHERE city ILIKE :city ORDER BY rating DESC")
    Flux<SchoolEntity> findByCityOrderByRatingDesc(String city);
}
