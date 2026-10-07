package com.nepbrain.technology.communication.controller;

import com.nepbrain.technology.communication.annotation.CommunicationRestController;
import com.nepbrain.technology.communication.payload.request.CommunicationRequest;
import com.nepbrain.technology.communication.payload.request.NotificationMessageRequest;
import com.nepbrain.technology.communication.service.CommunicationService;
import com.nepbrain.technology.communication.service.FirebaseNotificationService;
import com.nepbrain.technology.communication.util.ReactiveBlocking;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

@CommunicationRestController
@RequiredArgsConstructor
@Tag(name = "FCM Test", description = "FCM and email test endpoints")
public class FCMTestController {

    private final FirebaseNotificationService firebaseNotificationService;
    private final CommunicationService communicationService;

    @PostMapping("/fcm/test")
    @Operation(summary = "Send FCM test notification")
    public Mono<String> test(@RequestBody NotificationMessageRequest messageRequest) {
        return ReactiveBlocking.call(() -> firebaseNotificationService.sendNotification(messageRequest));
    }

    @PostMapping("/send-email/test")
    @Operation(summary = "Send test email")
    public Mono<String> testEmail(@RequestBody CommunicationRequest messageRequest) {
        return ReactiveBlocking.call(() -> communicationService.send(messageRequest));
    }
}
