package com.nepbrain.technology.communication.payload.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkSendResultResponse {
    private int totalUsers;
    private int emailSent;
    private int emailFailed;
    private int smsSent;
    private int smsFailed;
    private int notificationSent;
    private int notificationFailed;
}
