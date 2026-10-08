package com.nepbrain.technology.notification.payload.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {

    private UUID id;
    private UUID customerId;
    private String type;
    private String subject;
    private String message;
    private Boolean isRead;
    private UUID dataId;
}
