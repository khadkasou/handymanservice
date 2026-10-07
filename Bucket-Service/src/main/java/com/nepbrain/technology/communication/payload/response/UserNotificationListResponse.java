package com.nepbrain.technology.communication.payload.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserNotificationListResponse {
    private List<UserNotificationResponse> result;
    private long totalElements;
    private long unreadCount;
}
