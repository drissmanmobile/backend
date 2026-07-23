package com.drissman.service;

import com.drissman.adapters.inbound.rest.dto.NotificationDto;
import com.drissman.adapters.outbound.persistence.entity.DeviceTokenEntity;
import com.drissman.adapters.outbound.persistence.entity.NotificationEntity;
import com.drissman.adapters.outbound.persistence.repository.SpringDataDeviceTokenRepository;
import com.drissman.adapters.outbound.persistence.repository.SpringDataNotificationRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseNotificationService {

    private final SpringDataDeviceTokenRepository deviceTokenRepository;
    private final SpringDataNotificationRepository notificationRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Mono<Void> registerToken(UUID userId, String token, String platform) {
        return deviceTokenRepository.findByToken(token)
                .switchIfEmpty(Mono.defer(() -> {
                    DeviceTokenEntity entity = DeviceTokenEntity.builder()
                            .id(UUID.randomUUID())
                            .userId(userId)
                            .token(token)
                            .platform(platform)
                            .build();
                    return deviceTokenRepository.save(entity);
                }))
                .flatMap(existingEntity -> {
                    if (!existingEntity.getUserId().equals(userId)) {
                        existingEntity.setUserId(userId);
                        return deviceTokenRepository.save(existingEntity);
                    }
                    return Mono.just(existingEntity);
                })
                .doOnSuccess(saved -> log.info("Token registered for user {}", userId))
                .then();
    }

    public Mono<Void> unregisterToken(String token) {
        return deviceTokenRepository.deleteByToken(token)
                .doOnSuccess(v -> log.info("Token unregistered: {}", token));
    }

    public Flux<NotificationDto> getUserNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .map(this::toDto);
    }

    public Mono<Void> markAsRead(UUID id, UUID userId) {
        return notificationRepository.findById(id)
                .filter(n -> n.getUserId().equals(userId))
                .flatMap(n -> {
                    n.setRead(true);
                    return notificationRepository.save(n);
                })
                .then();
    }

    public Mono<Void> markAllAsRead(UUID userId) {
        return notificationRepository.markAllAsReadByUserId(userId);
    }

    public Mono<Void> sendNotificationToUser(UUID userId, String title, String body) {
        return sendNotificationToUser(userId, title, body, "admin");
    }

    public Mono<Void> sendNotificationToUser(UUID userId, String title, String body, String type) {
        NotificationEntity notification = NotificationEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .type(type != null ? type : "admin")
                .title(title)
                .message(body)
                .dateStr(LocalDateTime.now().format(DATE_FORMATTER))
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        return notificationRepository.save(notification)
                .then(deviceTokenRepository.findByUserId(userId)
                        .flatMap(deviceToken -> sendNotificationToToken(deviceToken.getToken(), title, body))
                        .then());
    }

    private Mono<Void> sendNotificationToToken(String token, String title, String body) {
        return Mono.fromCallable(() -> {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            return FirebaseMessaging.getInstance().send(message);
        })
        .subscribeOn(Schedulers.boundedElastic())
        .doOnSuccess(response -> log.info("Successfully sent message: {}", response))
        .doOnError(error -> log.error("Error sending FCM message: {}", error.getMessage()))
        .onErrorResume(e -> Mono.empty())
        .then();
    }

    private NotificationDto toDto(NotificationEntity entity) {
        return NotificationDto.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .type(entity.getType())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .date(entity.getDateStr() != null ? entity.getDateStr() : 
                      (entity.getCreatedAt() != null ? entity.getCreatedAt().format(DATE_FORMATTER) : ""))
                .read(entity.isRead())
                .build();
    }
}
