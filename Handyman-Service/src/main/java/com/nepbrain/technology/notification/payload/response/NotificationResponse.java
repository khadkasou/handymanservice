package com.nepbrain.technology.notification.payload.response;
import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List; import java.util.Map; import java.util.UUID;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class NotificationResponse {
private UUID id; private UUID customerId; private String type; private String subject; private String message; private Boolean isRead; private UUID dataId;
}
