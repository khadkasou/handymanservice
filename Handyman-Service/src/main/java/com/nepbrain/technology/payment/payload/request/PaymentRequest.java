package com.nepbrain.technology.payment.payload.request;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {

    private UUID bookingId;
    private UUID customerId;
    private BigDecimal amount;
    private String paymentType;
    private String txnId;
    private String paymentStatus;
}
