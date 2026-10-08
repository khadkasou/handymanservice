package com.nepbrain.technology.notification.service.impl;

import com.nepbrain.technology.notification.entity.NotificationEntity;
import com.nepbrain.technology.notification.payload.response.NotificationResponse;
import com.nepbrain.technology.notification.repository.NotificationRepository;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import com.nepbrain.technology.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public GlobalResponse notificationList(UUID customerId) {
        List<NotificationResponse> notifications = new ArrayList<>();
        for (NotificationEntity notification : notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)) {
            notifications.add(NotificationResponse.builder()
                    .id(notification.getId())
                    .customerId(notification.getCustomerId())
                    .type(notification.getType())
                    .subject(notification.getSubject())
                    .message(notification.getMessage())
                    .isRead(notification.getIsRead())
                    .dataId(notification.getDataId())
                    .build());
        }
        return GlobalResponseBuilder.buildSuccessResponse(notifications);
    }
}
