package com.nepbrain.technology.notification.controller;
import com.nepbrain.technology.notification.annotation.CustomNotificationRestController;
import com.nepbrain.technology.notification.service.NotificationService;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
import java.util.Map; import java.util.UUID;

@CustomNotificationRestController @RequiredArgsConstructor
@Tag(name = "Notification", description = "Notification APIs")
public class NotificationController {
    private final NotificationService notificationService;
    @PostMapping("/notification-list") public ResponseEntity<GlobalResponse> notificationList(
            @RequestParam(value = "customer_id", required = false) UUID customerId,
            @RequestBody(required = false) Map<String, Object> body) {
        return ResponseEntity.ok(GlobalResponseBuilder.buildSuccessResponse("NOTIFICATION_LIST",
                notificationService.notificationList(customerId)));
    }
}
