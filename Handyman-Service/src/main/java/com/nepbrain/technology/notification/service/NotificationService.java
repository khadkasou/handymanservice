package com.nepbrain.technology.notification.service;

import com.nepbrain.technology.core.payload.response.GlobalResponse;

import java.util.UUID;

public interface NotificationService {

    GlobalResponse notificationList(UUID customerId);
}
