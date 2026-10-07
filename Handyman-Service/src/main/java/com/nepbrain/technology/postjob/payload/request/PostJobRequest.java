package com.nepbrain.technology.postjob.payload.request;
import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List; import java.util.Map; import java.util.UUID;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class PostJobRequest {
private UUID id; private String title; private String description; private UUID customerId; private String status; private BigDecimal price; private String address; private UUID categoryId;
}
