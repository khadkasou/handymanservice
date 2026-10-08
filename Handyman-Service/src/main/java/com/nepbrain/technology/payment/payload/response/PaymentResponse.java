package com.nepbrain.technology.payment.payload.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {

    private UUID id;
    private UUID bookingId;
    private UUID customerId;
    private BigDecimal amount;
    private String paymentType;
    private String txnId;
    private String paymentStatus;
    private String datetime;
}
