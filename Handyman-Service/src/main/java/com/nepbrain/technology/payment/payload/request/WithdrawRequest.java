package com.nepbrain.technology.payment.payload.request;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WithdrawRequest {

    private UUID userId;
    private UUID bankId;
    private BigDecimal amount;
}
