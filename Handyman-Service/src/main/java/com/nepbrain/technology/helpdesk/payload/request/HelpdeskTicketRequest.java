package com.nepbrain.technology.helpdesk.payload.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HelpdeskTicketRequest {

    private UUID id;
    private UUID customerId;
    private String subject;
    private String description;
    private String status;
    private String mode;
    private String type;
}
