package com.nepbrain.technology.helpdesk.payload.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HelpdeskActivityRequest {

    private UUID helpdeskId;
    private UUID senderId;
    private String messages;
    private String senderType;
}
