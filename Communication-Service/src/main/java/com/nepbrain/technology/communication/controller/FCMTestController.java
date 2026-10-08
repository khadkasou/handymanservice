package com.nepbrain.technology.communication.controller;

/*
 * @Created At 18/11/2024
 * @Author Suroj Awal
 */

import com.google.firebase.messaging.FirebaseMessagingException;
import com.nepbrain.technology.communication.annotation.CommunicationRestController;
import com.nepbrain.technology.communication.payload.request.CommunicationRequest;
import com.nepbrain.technology.communication.payload.request.NotificationMessageRequest;
import com.nepbrain.technology.communication.service.CommunicationService;
import com.nepbrain.technology.communication.service.FirebaseNotificationService;
import com.nepbrain.technology.core.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CommunicationRestController
@RequiredArgsConstructor
public class FCMTestController {

    private final FirebaseNotificationService firebaseNotificationService;
    private final CommunicationService communicationService;

    @PostMapping("/fcm/test")
    public String test(@RequestBody NotificationMessageRequest messageRequest)
            throws FirebaseMessagingException {
        return firebaseNotificationService.sendNotification(messageRequest);

    }

    @PostMapping("/send-email/test")
    public String test(@RequestBody CommunicationRequest messageRequest) throws GlobalException {
        return String.valueOf(communicationService.send(messageRequest).getData());

    }

}
