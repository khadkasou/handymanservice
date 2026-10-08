package com.nepbrain.technology.payment.payload.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletResponse {

    private UUID id;
    private UUID userId;
    private BigDecimal amount;
}
