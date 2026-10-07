package com.nepbrain.technology.communication.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateUserNotificationRequest {

    @NotNull
    private UUID recipientUserId;

    private UUID recipientEmployeeId;
    private UUID companyId;
    private UUID branchId;

    @NotBlank
    private String title;

    @NotBlank
    private String body;

    private String topic;
    private String notificationType;
    private String category;
    private String referenceType;
    private String referenceId;
    private String requestType;
    private UUID requestId;
    private String requestNumber;
    private UUID workflowHistoryId;
    private String actionUrl;
    private String actionLabel;
    private String priority;
    private String channel;
    private String metadata;

    private Boolean sendEmail;
    private String emailSubject;
    private String emailBody;
}
