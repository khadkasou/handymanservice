package com.nepbrain.technology.communication.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UserNotificationResponse {
    private UUID id;
    private UUID userId;
    private String title;
    private String body;
    @JsonProperty("read")
    private boolean read;
    private String topic;
    private String notificationType;
    private String category;
    private String referenceType;
    private String referenceId;
    private String requestType;
    private UUID requestId;
    private String actionUrl;
    private String actionLabel;
    private String metadata;
    private UUID recipientEmployeeId;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
