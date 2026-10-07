package com.nepbrain.technology.payment.payload.response;
import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List; import java.util.Map; import java.util.UUID;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class PaymentGatewayResponse {
private UUID id; private String title; private String type; private Boolean isTest; private String status;
}
