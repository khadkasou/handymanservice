package com.nepbrain.technology.communication.service;

import com.nepbrain.technology.communication.payload.request.CreateUserNotificationRequest;
import com.nepbrain.technology.communication.payload.request.UserNotificationListRequest;
import com.nepbrain.technology.communication.payload.response.UserNotificationListResponse;
import com.nepbrain.technology.communication.payload.response.UserNotificationUnreadCountResponse;

import java.util.UUID;

public interface UserNotificationService {

    UserNotificationListResponse listForCurrentUser(UserNotificationListRequest request);

    UserNotificationListResponse listForUser(UUID userId, UserNotificationListRequest request);

    UserNotificationListResponse listWorkflowForUser(UUID userId, UserNotificationListRequest request);

    UserNotificationUnreadCountResponse unreadCountForCurrentUser();

    UserNotificationUnreadCountResponse unreadCountForUser(UUID userId);

    UserNotificationUnreadCountResponse workflowUnreadCountForUser(UUID userId);

    void markRead(UUID notificationId);

    void markReadForUser(UUID userId, UUID notificationId);

    void markAllRead();

    void markAllReadForUser(UUID userId);

    void create(CreateUserNotificationRequest request);
}
