package com.nepbrain.technology.helpdesk.payload.response;
import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List; import java.util.Map; import java.util.UUID;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class HelpdeskActivityResponse {
private UUID id; private UUID helpdeskId; private UUID senderId; private String messages; private String senderType; private String activityType;
}
