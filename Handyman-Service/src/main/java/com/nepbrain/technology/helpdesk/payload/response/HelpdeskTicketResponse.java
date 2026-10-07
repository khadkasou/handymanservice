package com.nepbrain.technology.helpdesk.payload.response;
import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List; import java.util.Map; import java.util.UUID;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class HelpdeskTicketResponse {
private UUID id; private UUID customerId; private String subject; private String description; private String status; private String mode; private String type; private List<HelpdeskActivityResponse> activities;
}
