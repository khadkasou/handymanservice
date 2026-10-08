package com.nepbrain.technology.payment.payload.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletTransactionResponse {

    private UUID id;
    private UUID userId;
    private BigDecimal amount;
    private String type;
    private String activityType;
    private String datetime;
}
