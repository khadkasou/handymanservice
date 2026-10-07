package com.nepbrain.technology.helpdesk.payload.request;
import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List; import java.util.Map; import java.util.UUID;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class HelpdeskActivityRequest {
private UUID helpdeskId; private UUID senderId; private String messages; private String senderType;
}
