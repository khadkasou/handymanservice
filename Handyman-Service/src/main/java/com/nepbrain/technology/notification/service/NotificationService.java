package com.nepbrain.technology.notification.service;
import com.nepbrain.technology.notification.payload.response.NotificationResponse;
import java.util.List; import java.util.UUID;
public interface NotificationService {
    List<NotificationResponse> notificationList(UUID customerId);
}
