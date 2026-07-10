package com.drissman.service;

import com.drissman.adapters.outbound.persistence.entity.DeviceTokenEntity;
import com.drissman.adapters.outbound.persistence.repository.SpringDataDeviceTokenRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseNotificationService {

    private final SpringDataDeviceTokenRepository deviceTokenRepository;

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

    public Mono<Void> sendNotificationToUser(UUID userId, String title, String body) {
        return deviceTokenRepository.findByUserId(userId)
                .flatMap(deviceToken -> sendNotificationToToken(deviceToken.getToken(), title, body))
                .then();
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
}
