package com.drissman.adapters.inbound.rest;

import com.drissman.adapters.inbound.rest.dto.ApiResponse;
import com.drissman.adapters.inbound.rest.dto.NotificationDto;
import com.drissman.adapters.inbound.rest.dto.RegisterDeviceTokenRequest;
import com.drissman.domain.model.User;
import com.drissman.service.FirebaseNotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final FirebaseNotificationService notificationService;

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<List<NotificationDto>>>> getUserNotifications(
            @AuthenticationPrincipal User user) {
        return notificationService.getUserNotifications(user.getId())
                .collectList()
                .map(list -> ResponseEntity.ok(ApiResponse.ok(list, "Notifications fetched successfully")));
    }

    @PatchMapping("/{id}/read")
    public Mono<ResponseEntity<ApiResponse<Void>>> markAsRead(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return notificationService.markAsRead(id, user.getId())
                .thenReturn(ResponseEntity.ok(ApiResponse.ok(null, "Notification marked as read")));
    }

    @PostMapping("/read-all")
    public Mono<ResponseEntity<ApiResponse<Void>>> markAllAsRead(
            @AuthenticationPrincipal User user) {
        return notificationService.markAllAsRead(user.getId())
                .thenReturn(ResponseEntity.ok(ApiResponse.ok(null, "All notifications marked as read")));
    }

    @PostMapping("/register-token")
    public Mono<ResponseEntity<ApiResponse<Void>>> registerToken(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody RegisterDeviceTokenRequest request) {

        return notificationService.registerToken(user.getId(), request.getToken(), request.getPlatform())
                .thenReturn(ResponseEntity.ok(ApiResponse.ok(null, "Token registered successfully")));
    }

    @DeleteMapping("/unregister-token/{token}")
    public Mono<ResponseEntity<ApiResponse<Void>>> unregisterToken(
            @AuthenticationPrincipal User user,
            @PathVariable String token) {

        return notificationService.unregisterToken(token)
                .thenReturn(ResponseEntity.ok(ApiResponse.ok(null, "Token unregistered successfully")));
    }
}
