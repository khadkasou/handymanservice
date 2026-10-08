package com.nepbrain.technology.payment.payload.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentGatewayResponse {

    private UUID id;
    private String title;
    private String type;
    private Boolean isTest;
    private String status;
}
