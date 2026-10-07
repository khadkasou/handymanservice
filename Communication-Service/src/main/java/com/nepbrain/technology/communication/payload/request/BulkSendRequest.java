package com.nepbrain.technology.communication.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

/** Request to send bulk SMS, email, and/or notification to users (all, by role, or selected). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkSendRequest {

    public enum TargetType { ALL, ROLE, SELECTED }

    @NotNull
    private TargetType targetType;

    /** Role UUIDs when targetType is ROLE. */
    private List<String> roleIds;

    /** User UUIDs when targetType is SELECTED. */
    private List<String> userIds;

    private boolean sendEmail;
    private boolean sendSms;
    private boolean sendNotification;

    private String emailSubject;
    private String emailBody;

    private String smsBody;

    private String notificationTitle;
    private String notificationBody;
}
