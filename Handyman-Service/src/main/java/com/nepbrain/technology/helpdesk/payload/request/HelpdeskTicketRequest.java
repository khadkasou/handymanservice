package com.nepbrain.technology.helpdesk.payload.request;
import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List; import java.util.Map; import java.util.UUID;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class HelpdeskTicketRequest {
private UUID id; private UUID customerId; private String subject; private String description; private String status; private String mode; private String type;
}
