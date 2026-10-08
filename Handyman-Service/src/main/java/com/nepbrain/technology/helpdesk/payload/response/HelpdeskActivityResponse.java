package com.nepbrain.technology.helpdesk.payload.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HelpdeskActivityResponse {

    private UUID id;
    private UUID helpdeskId;
    private UUID senderId;
    private String messages;
    private String senderType;
    private String activityType;
}
