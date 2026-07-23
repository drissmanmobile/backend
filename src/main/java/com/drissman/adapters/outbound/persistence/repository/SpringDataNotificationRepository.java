package com.drissman.adapters.outbound.persistence.repository;

import com.drissman.adapters.outbound.persistence.entity.NotificationEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface SpringDataNotificationRepository extends R2dbcRepository<NotificationEntity, UUID> {
    Flux<NotificationEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);

    @Query("UPDATE notifications SET read = true WHERE user_id = :userId")
    Mono<Void> markAllAsReadByUserId(UUID userId);
}
