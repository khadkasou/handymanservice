package com.nepbrain.technology.payment.payload.response;
import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List; import java.util.Map; import java.util.UUID;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class PaymentResponse {
private UUID id; private UUID bookingId; private UUID customerId; private BigDecimal amount; private String paymentType; private String txnId; private String paymentStatus; private String datetime;
}
