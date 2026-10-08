package com.nepbrain.technology.notification.controller;

import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.notification.annotation.CustomNotificationRestController;
import com.nepbrain.technology.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.UUID;

@CustomNotificationRestController
@RequiredArgsConstructor
@Tag(name = "Notification")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/notification-list")
    @Operation(summary = "Notification list")
    public ResponseEntity<GlobalResponse> notificationList(@RequestParam(value = "customer_id", required = false) UUID customerId,
                                                           @RequestBody(required = false) Map<String, Object> body) {
        return ResponseEntity.ok().body(notificationService.notificationList(customerId));
    }
}
