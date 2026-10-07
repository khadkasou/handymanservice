package com.nepbrain.technology.communication.payload.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserNotificationUnreadCountResponse {
    private long unreadCount;
    private long count;
}
