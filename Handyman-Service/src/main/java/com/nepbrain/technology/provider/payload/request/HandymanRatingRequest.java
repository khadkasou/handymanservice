package com.nepbrain.technology.provider.payload.request;
import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List; import java.util.Map; import java.util.UUID;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class HandymanRatingRequest {
private UUID id; private UUID handymanId; private UUID customerId; private UUID bookingId; private Double rating; private String review;
}
