package com.nepbrain.technology.payment.payload.request;
import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List; import java.util.Map; import java.util.UUID;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class WalletTopUpRequest {
private UUID userId; private BigDecimal amount; private String paymentType; private String txnId;
}
