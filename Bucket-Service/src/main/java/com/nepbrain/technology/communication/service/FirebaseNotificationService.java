package com.nepbrain.technology.communication.service;

/*
 * @Created At 05/09/2024
 * @Author suroj.awal
 */

import com.google.firebase.messaging.*;
import com.nepbrain.technology.communication.payload.request.BulkNotificationWithSameMessageRequest;
import com.nepbrain.technology.communication.payload.request.NotificationMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FirebaseNotificationService {

    private final FirebaseMessaging firebaseMessaging;

    @Value("${firebase.batchSize}")
    private Integer batchSize;

    private static final String ERROR = "Error: ";

    public FirebaseNotificationService(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    public String sendNotification(NotificationMessageRequest request) throws FirebaseMessagingException {

        Notification notification = Notification.builder()
                .setTitle(request.getTitle())
                .setBody(request.getBody())
                .setImage(request.getImage())
                .build();

        Message message = Message.builder()
                .setToken(request.getFcmId())
                .setNotification(notification)
                .putAllData(request.getData() != null ? request.getData() : java.util.Collections.emptyMap())
                .build();

        return firebaseMessaging.send(message);
    }

    public List<String> sendBulkNotificationsWithDiffMessage(List<NotificationMessageRequest> requests) {
        List<String> results = new ArrayList<>();

        try {
            for (int i = 0; i < requests.size(); i += batchSize) {
                // Process the batch
                List<NotificationMessageRequest> batch = requests.subList(i, Math.min(i + batchSize, requests.size()));
                List<Message> messages = new ArrayList<>();

                for (NotificationMessageRequest request : batch) {
                    Notification notification = Notification.builder()
                            .setTitle(request.getTitle())
                            .setBody(request.getBody())
                            .setImage(request.getImage())
                            .build();

                    Message message = Message.builder()
                            .setToken(request.getFcmId())
                            .setNotification(notification)
                            .putAllData(request.getData() != null ? request.getData() : java.util.Collections.emptyMap())
                            .build();

                    messages.add(message);
                }

                // Send messages using sendEach
                List<BatchResponse> batchResponses = new ArrayList<>();
                for (Message message : messages) {
                    batchResponses.add(firebaseMessaging.sendEach(List.of(message)));
                }

                // Collect responses
                for (BatchResponse response : batchResponses) {
                    response.getResponses()
                            .forEach(r -> {
                                if (r.isSuccessful()) {
                                    results.add(r.getMessageId());
                                } else {
                                    results.add(ERROR + r.getException()
                                            .getMessage());
                                }
                            });
                }
            }
        } catch (Exception e) {
            results.add(ERROR + e.getMessage());
        }

        return results;
    }

    public void sendBulkNotificationWithSameMessage(
            BulkNotificationWithSameMessageRequest bulkNotificationWithSameMessageRequest) {
        List<String> results = new ArrayList<>();
        List<String> fcmIds = bulkNotificationWithSameMessageRequest.getFcmIds();
        NotificationMessageRequest request = bulkNotificationWithSameMessageRequest.getMessageRequest();
        // Build notification
        Notification notification = Notification.builder()
                .setTitle(request.getTitle())
                .setBody(request.getBody())
                .setImage(request.getImage())
                .build();

        // Build multicast message
        MulticastMessage multicastMessage = MulticastMessage.builder()
                .addAllTokens(fcmIds)
                .setNotification(notification)
                .putAllData(request.getData() != null ? request.getData() : java.util.Collections.emptyMap())
                .build();

        try {
            // Send the multicast message using sendEachForMulticast
            BatchResponse response = firebaseMessaging.sendEachForMulticast(multicastMessage);

            // Process and collect response results
            response.getResponses()
                    .forEach(r -> {
                        if (r.isSuccessful()) {
                            results.add(r.getMessageId());
                        } else {
                            results.add(ERROR + r.getException()
                                    .getMessage());
                        }
                    });
        } catch (Exception e) {
            results.add(ERROR + e.getMessage());
        }
        log.info("Size: " + results.size());

    }

}
